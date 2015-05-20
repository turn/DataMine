/**
 * Copyright (C) 2015 Turn Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package datamine.storage.recordbuffers;

import java.nio.ByteBuffer;
import java.util.List;

import com.google.common.base.Preconditions;

import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Field;
import datamine.storage.idl.FieldValueOperatorInterface;
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.idl.type.PrimitiveType;
import datamine.storage.recordbuffers.idl.value.FieldValueOperatorFactory;

/**
 * The definition of record for each tuple stored in the table. 
 * 
 * @author yqi
 * @date Jan 12, 2015
 */
public class Record<T extends Enum<T> & RecordMetadataInterface> {
	
	/**
	 * To improve writing performance, the buffer is kept when the length
	 * of the buffer is smaller the following threshold.
	 */
	private final int RESERVE_BUFFER_CAP = 0;//10000000; 
	
	private final RecordOperator<T> operator;
	
	private RecordBuffer buffer = null;
	private boolean hasNewValues = false;
	private Object[] valueArray = null;
	
	public Record(Class<T> clazz) {
		operator = RecordOperator.getRecordOperator(clazz);
	}
	
	/**
	 * Constructor. 
	 * 
	 * <p>
	 * Note that the input recordbuffer instance can be changed by the record. 
	 * </p>
	 * 
	 * @param clazz the class indicating the record metadata
	 * @param buf the instance of {@link RecordBuffer}
	 */
	public Record(Class<T> clazz, RecordBuffer buf) {
		this(clazz);
		
		Preconditions.checkArgument(buf != null);
		this.buffer = buf;
	}
	
	/**
	 * Get the record buffer containing the record.
	 * 
	 * <p>
	 * The original record buffer if exists may be returned directly, when
	 * there is no update.
	 * </p>
	 * <p>
	 * TODO: It is possible to improve the memory usage here!
	 * </p>
	 * 
	 * @return the record buffer containing the record
	 */
	public RecordBuffer getRecordBuffer() {
		if (valueArray != null && hasNewValues) {
			// in case the buffer is not cleared yet
			if (this.buffer != null) {
				this.buffer.clear();
			}
			
			// create the record buffer from the object array
			this.buffer = operator.getRecordBuffer(valueArray);
			
			// clean up the flag and the object array
			this.valueArray = null;
			this.hasNewValues = false;
		} 
		return buffer;
	}
	
	/**
	 * @return The number of bytes that all values take
	 */
	public int getNumOfBytes() {
		if (valueArray != null) {
			return (Integer) valueArray[valueArray.length - 1];
		} else if (buffer != null) {
			return buffer.getRecordBufferSize();
		} else {
			return 0;
		}
	}
	
	/**
	 * @return An array of bytes to contain all values
	 */
	public byte[] array() {
		// first create the record buffer if necessary
		getRecordBuffer();
		if (buffer != null) {
			return buffer.getByteBuffer().array();
		} else {
			return new byte[0];
		}
	}
	
	/**
	 * Initiate the value array for all valid values in the record buffer.
	 * 
	 * <p>
	 * It happens with any updates for the current record. 
	 * </p>
	 */
	protected void initValueArray() {
		if (buffer != null && this.buffer.getRecordBufferSize() > 0) {
			this.valueArray = this.operator.getValueArray(buffer.getByteBuffer(), 0);
			if (buffer.getRecordBufferSize() > RESERVE_BUFFER_CAP) {
				this.buffer.clear();
				this.buffer = null;
				this.hasNewValues = true; // Fake for {@getRecordBuffer()}
			}
		} else {
			if (this.valueArray == null) {
				this.valueArray = new Object[1 + operator.getTableSize()];
				valueArray[operator.getTableSize()] = operator.getMaxHeaderSize();
			}
		}
	}
	
	/**
	 * Get the number of elements in the collection-type field. 
	 * 
	 * @param col the input of a collection-type field.
	 * @return the number of elements in the collection-type field.
	 */
	@SuppressWarnings("rawtypes")
	public int getListSize(T col) {
		//1. the input must be a collection-type field
		Preconditions.checkArgument(
				col.getField().getType() instanceof CollectionFieldType);
		//2. read the size directly from the byte array with the offset
		if (valueArray == null) {
			return this.operator.getCollectionSize(col, buffer.getByteBuffer(), 0);
		}
		//3. read the size from the intermediate object array
		int id = col.getField().getId() - 1;
		if (this.valueArray[id] != null) {
			return ((List) this.valueArray[id]).size();	
		} else {
			return 0;
		}
	}
	
	/**
	 * Get the value of the input column
	 * @param col the input field
	 * @return the value of the input column
	 */
	public Object getValue(T col) {
	
		Field field = col.getField();
		// when the column is sort-key or with 'hasRef' annotation 
		if ((field.isSortKey() || field.isFrequentlyUsed()) && valueArray == null && this.buffer.getRecordBufferSize() > 0) {
			FieldType type = field.getType();
			ByteBuffer buf = buffer.getByteBuffer();
			// find out the offset directly
			int offset = field.isSortKey() ? 
					operator.getSortKeyOffset(buf, 0) :
					operator.getFieldWithReferenceOffset(col, buf, 0);
			FieldValueOperatorInterface valueOpr = FieldValueOperatorFactory.getOperator(type);
			if (type instanceof PrimitiveFieldType) {
				PrimitiveType priType = ((PrimitiveFieldType) type).getType();
				switch (priType) {
				case STRING:
					return valueOpr.getValue(buf, offset + 2, buf.getShort(offset));
				case BINARY:
					return valueOpr.getValue(buf, offset + 4, buf.getInt(offset));
				default:
					return valueOpr.getValue(buf, offset, valueOpr.getNumOfBytes(null));
				}
			} else if (type instanceof GroupFieldType || type instanceof CollectionFieldType) {
				return valueOpr.getValue(buf, offset + 4, buf.getInt(offset));
			} 
		}
		// for fields without 'hasRef' annotation, a full deserialization is applied
		if (valueArray == null) {
			initValueArray();
		}
		
		int id = field.getId() - 1; // note that id starts at 1.
 		Object result = valueArray != null && valueArray.length > id + 1 ? valueArray[id] : null;
		if (result == null) { // never return NULL
			return col.getField().getDefaultValue();
		} else {
			return result;
		}
	}
	
	/**
	 * Update the value of the input field
	 * 
	 * @param col the field of interest
	 * @param val the new value of the concerned field
	 */
	public void setValue(T col, Object val) {
		if (val != null) {
			//1. prepare the intermediate structure 
			if (valueArray == null) {
				initValueArray();	
			}
			//2. find out the number of bytes used for new value
			int id = col.getField().getId() - 1; // note that id starts at 1
			int size = 0;
			FieldType type = col.getField().getType();
			FieldValueOperatorInterface valOpr = FieldValueOperatorFactory.getOperator(type);
			int sizeId = valueArray.length - 1;
			int length = (Integer) valueArray[sizeId];

			if (valueArray[id] != null) {
				size = valOpr.getNumOfBytes(valueArray[id]);				
			} else {
				// add the overhead of the value
				length += valOpr.getMetadataSize();
			}
			//3. update the value in the intermediate structure
			valueArray[id] = val;
			
			//4. determine the difference of sizes for serialization
			valueArray[sizeId] = length + valOpr.getNumOfBytes(val) - size;
			hasNewValues = true;
			if (this.buffer != null) {
				this.buffer.clear();
				this.buffer = null;
			}
		}
	}
}
