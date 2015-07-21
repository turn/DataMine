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
package datamine.storage.recordbuffers.pax.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Field;
import datamine.storage.idl.type.FieldType;
import datamine.storage.recordbuffers.Record;
import datamine.storage.recordbuffers.RecordBuffer;
import datamine.storage.recordbuffers.RecordBufferMeta;
import datamine.storage.recordbuffers.pax.encoding.EncodingScheme;

/**
 * @author yqi
 */
public class PaxInRecordBuffer<T extends Enum<T> & RecordMetadataInterface> {

	public static final Logger LOG = LoggerFactory.getLogger(PaxInRecordBuffer.class);
	
	private Map<T, List<Object>> values = null; // used for PAX-format
	private Map<T, int[]> fieldIndex = null; // offset of columns (int[]{offset,length})
	
	/**
	 * The storage metadata about the recordbuffer is final
	 */
	protected final RecordBufferMeta<T> meta; 
	
	public PaxInRecordBuffer(Class<T> clazz) {
		this.meta = RecordBufferMeta.getRecordOperator(clazz);
	}
	
	/**
	 * It stores a list of records in the format of PAX. 
	 * 
	 * <p>
	 * Note that the non-primitive field (or BINARY primitive field) is stored as 
	 * it is without any compression. 
	 * Also there should not be nulls in the input value list.
	 * </p>
	 * 
	 * <p>
	 * It may not be a good idea to apply the PAX approach is the input list is 
	 * small (e.g., less than a couple of hundreds), as the overhead by the
	 * encoding/decoding may overkill the performance. 
	 * </p>
	 * 
	 * @param recordList a list of records in the format of {@link RecordBuffer}
	 * @return an instance of {@link RecordBuffer} to store a list of records in the PAX format
	 */
	public RecordBuffer getRecordBufferInPax(List<Record<T>> recordList) {
				
		RecordBuffer rb = new RecordBuffer();
		
		if (recordList == null || recordList.isEmpty()) {
			return rb; // an empty result if no input is meaningful
		}
		
		List<T> fieldList = this.meta.getFieldList();
		int numOfFields = fieldList.size();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		// 
		try {
			//1. set values for header : type id, # of fields, and # of records
			short paxTypeId = -1;
			outStream.write(ByteBuffer.allocate(2).putShort(paxTypeId).array());
			short numOfField = (short) fieldList.size();
			outStream.write(ByteBuffer.allocate(2).putShort(numOfField).array());
			int numOfRecord = recordList.size();
			outStream.write(ByteBuffer.allocate(4).putInt(numOfRecord).array());
			
			//2. collect values of each field
			int[] offsets = new int[numOfFields];
			int offset = 8;
			for (int j=0; j<numOfFields; ++j) {
				T curAttr = fieldList.get(j);
				Field field = curAttr.getField();
				FieldType type = field.getType();
				
				//2.1 prepare the array to store values
				offsets[j] = offset;
				List<Object> values = Lists.newArrayList();
				for (int i=0; i<numOfRecord; ++i) {
					values.add(recordList.get(i).getValue(curAttr));
				}
				
				//2.2 collect the stats to decide the best scheme to encode
				byte[] content = EncodingScheme.getBytes(values, type);
				outStream.write(content);
				offset += content.length;
			}
			
			//3. put the footer finally
			ByteBuffer buf = ByteBuffer.allocate(4 * numOfFields);
			for (int i=0; i<offsets.length; ++i) {
				buf.putInt(offsets[i]);
			}
			outStream.write(buf.array());
			byte[] outByteArray = outStream.toByteArray();
			rb.wrap(outByteArray, 0, outByteArray.length);
			 
		} catch (IOException e) {
			LOG.error("Cannot open a stream to convert the input array to a byte array!");
		}
		
		return rb;
	}
	
//	public RecordBuffer getRecordBufferInPax(List<Record<T>> recordList)
	
	public Object getValueArray(RecordBuffer rb, FieldType type) {
		Object result = null;
		
		
		
		return result;
	}
	
//	/**
//	 * Calculate the offsets of attribute columns in the byte buffer.
//	 * 
//	 * Note: it must be called before the method {getCompressedColumnValues(ByteBuffer, T)} 
//	 * given a new record. 
//	 * 
//	 * @param buffer the byte buffer containing the record
//	 */
//	private void getCompressIndex() {
//		fieldIndex.clear();
//		ByteBuffer buf = buffer.getByteBuffer();
//		
//		//1. get the index of all columns
//		short colNum = buf.getShort(2);
//		int offset = buf.array().length - 4 * colNum;
//		for (int i = 0; i < colNum; i++) {
//			int pos = buf.getInt(offset);
//			if (pos > 0) {
//				int[] pair = new int[] {pos, 0};
//				fieldIndex.put(this.meta.getFieldList().get(i), pair);				
//			}
//			offset += 4;
//		}
//	}
//	
//	/**
//	 * Store the input collection (a list of tuples) into a byte buffer in the 
//	 * format of PAX.
//	 * 
//	 * @param input a list of maps of all attributes
//	 * @return the byte buffer contains the input list in the PAX format
//	 */
//	public ByteBuffer getByteBuffer(List<Map<T, Object>> inputList,
//			int bufSize) {
//		Comparator comparator = null;
//		if (!inputList.isEmpty() && !inputList.get(0).keySet().isEmpty()) {
//			T col = inputList.get(0).keySet().iterator().next();
//			comparator = col.getComparator();
//		}
//		return getByteBuffer(inputList, bufSize, comparator);
//	}
//	/**
//	 * Store the input collection (a list of tuples) into a byte buffer in the 
//	 * format of PAX.
//	 * 
//	 * @param inputList a list of maps of all attributes
//	 * @param bufSize the initial size of the byte buffer
//	 * @param sorter comparator if the list requires sorting
//	 * @return the byte buffer contains the input list in the PAX format
//	 */
//	public ByteBuffer getByteBuffer(List<Map<T, Object>> inputList,
//			int bufSize, Comparator<Map<T, Object>> sorter) {
//		
//		if (sorter != null) {
//			Collections.sort(inputList, sorter);
//		}
//		
//		//0. Initiate the intermediate variables
//		int colNum = fieldList.size();
//		ByteBuffer out = ByteBuffer.allocate(bufSize);
//		out.putShort((short) colNum);
//		out.putInt(inputList.size());
//		
//		if (values == null) {
//			values = new EnumMap<T, List<Object>>(dummyClass);
//		} else {
//			values.clear();
//		}
//		
//		//1. collect the stats of the inputs
//		for (Map<T, Object> input : inputList) {
//			
//			for (int i=0; i<colNum; ++i) {
//			
//				T col = fieldList.get(i);
//							
//				//1.1 find out the value list for the column
//				List<Object> objectList = values.get(col);
//				if (objectList == null) {
//					objectList = new ArrayList<Object>();
//					values.put(col, objectList);
//				}
//				//1.2 put the valid or default value into the list
//				Object value = input.get(col);
//				if (value!=null && input.containsKey(col) 
//				&& col.getAttribute().getType().isValid(value) 
//				&& !col.getAttribute().equalToDefaultValue(value)) {
//					objectList.add(value);	
//				} else {
//					objectList.add(col.getAttribute().getDefaultValue());
//				}
//			}
//		}
//		
//		//2. Determine compression type for each column set and then write to buffer 
//		int[] offsetList = new int[colNum];
//		for (int i=0; i<colNum; ++i) {
//			
//			offsetList[i] = out.position();
//		
//			//2.1 get the frequency of the commonest value
//			T col = fieldList.get(i);
//			List<Object> valueList = (ArrayList<Object>) values.get(col);
//			if (valueList != null && !valueList.isEmpty()) {
//				EncodingScheme.appendToByteBuffer(out, valueList, col.getAttribute().getType());
//			} else {
//				offsetList[i] = -1;
//			}
//		}
//		
//		//3. add the indexing header
//		for (int i = 0; i < colNum; i++) {
//			out.putInt(offsetList[i]);
//		}
//
//		return out;
//	}
//	
	
//	/**
//	 * Update the value of the input field
//	 * 
//	 * TODO (Yan) support the deletion by making col null.
//	 * 
//	 * @param col the field of interest
//	 * @param val the new value of the concerned field
//	 */
//	@Override
//	public void setValue(T col, Object val) {
//		if (val != null) {
//			//1. prepare the intermediate structure 
//			if (valueArray == null) {
//				initValueArray();	
//			}
//			//2. find out the number of bytes used for new value
//			int id = col.getField().getId() - 1; // note that id starts at 1
//			int size = 0;
//			FieldType type = col.getField().getType();
//			FieldValueOperatorInterface valOpr = FieldValueOperatorFactory.getOperator(type);
//
//			if (valueArray[id] != null) {
//				size = valOpr.getNumOfBytes(valueArray[id]);				
//			} else {
//				// add the overhead of the value
//				this.numOfBytes += valOpr.getMetadataLength();
//			}
//			//3. update the value in the intermediate structure
//			valueArray[id] = val;
//			
//			//4. determine the difference of sizes for serialization
//			this.numOfBytes += valOpr.getNumOfBytes(val) - size;
//			hasNewValues = true;
//			if (this.buffer != null) {
//				this.buffer.clear();
//				this.buffer = null;
//			}
//		}
//	}
//
//	@Override
//	public Object getValue(T col) {
//	
//		if (valueArray == null && readOnlyRecord == null && buffer != null) {
//			readOnlyRecord = new ReadOnlyRecord<T>(meta.getTableEnumClass(), buffer);
//		}
//		
//		if (readOnlyRecord != null) {
//			return readOnlyRecord.getValue(col);
//		}
//				
//		Field field = col.getField();
//		int id = field.getId() - 1; // note that id starts at 1.
// 		Object result = valueArray != null && valueArray.length > id ? valueArray[id] : null;
//		if (result == null) { // never return NULL
//			return col.getField().getDefaultValue();
//		} else {
//			return result;
//		}
//	}
	
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
//	@Override
//	public RecordBuffer getRecordBuffer() {
//		if (valueArray != null && hasNewValues) {
//			// in case the buffer is not cleared yet
//			if (this.buffer != null) {
//				this.buffer.clear();
//			}
//			
//			// create the record buffer from the object array
//			constructRecordBufferFromValueArray();
//			
//			// clean up the flag and the object array
//			this.valueArray = null;
//			this.hasNewValues = false;
//		} 
//		return buffer;
//	}
//
//	@Override
//	public int getNumOfBytes() {
//		return this.numOfBytes;
//	}
//
//	@Override
//	public byte[] array() {
//		// first create the record buffer if necessary
//		getRecordBuffer();
//		if (buffer != null) {
//			return buffer.getByteBuffer().array();
//		} else {
//			return new byte[0];
//		}
//	}
//
//	@SuppressWarnings("rawtypes")
//	@Override
//	public int getListSize(T col) {
//		//1. the input must be a collection-type field
//		Preconditions.checkArgument(
//				col.getField().getType() instanceof CollectionFieldType);
//		//2. read the size directly from the byte array with the offset
//		if (valueArray == null) {
//			return this.meta.getCollectionSize(col, this.buffer);
//		}
//		//3. read the size from the intermediate object array
//		int id = col.getField().getId() - 1;
//		if (this.valueArray[id] != null) {
//			return ((List) this.valueArray[id]).size();	
//		} else {
//			return 0;
//		}
//	}
//	
//	//////////////////////////////////////////////////////////////////////////
//	
//	public boolean getBool(T col) {
//		if (readOnlyRecord != null) {
//			return readOnlyRecord.getBool(col);
//		}
//		return (Boolean) getValue(col);
//	}
//
//	
//	public byte getByte(T col) {
//		if (readOnlyRecord != null) {
//			return readOnlyRecord.getByte(col);
//		}
//		return (Byte) getValue(col);
//	}
//
//	
//	public short getShort(T col) {
//		if (readOnlyRecord != null) {
//			return readOnlyRecord.getShort(col);
//		}
//		return (Short) getValue(col);
//	}
//
//	
//	public long getLong(T col) {
//		if (readOnlyRecord != null) {
//			return readOnlyRecord.getLong(col);
//		}
//		return (Long) getValue(col);
//	}
//
//	
//	public int getInt(T col) {
//		if (readOnlyRecord != null) {
//			return readOnlyRecord.getInt(col);
//		}
//		return (Integer) getValue(col);
//	}
//
//	
//	public float getFloat(T col) {
//		if (readOnlyRecord != null) {
//			return readOnlyRecord.getFloat(col);
//		}
//		return (Float) getValue(col);
//	}
//
//	
//	public double getDouble(T col) {
//		if (readOnlyRecord != null) {
//			return readOnlyRecord.getDouble(col);
//		}
//		return (Double) getValue(col);
//	}
//
//	
//	public byte[] getBinary(T col) {
//		if (readOnlyRecord != null) {
//			return readOnlyRecord.getBinary(col);
//		}
//		return (byte[]) getValue(col);
//	}
//
//	
//	public String getString(T col) {
//		if (readOnlyRecord != null) {
//			return readOnlyRecord.getString(col);
//		}
//		return (String) getValue(col);
//	}
	
	///////////////////////////////////////////////////////////////////////////
	// The following getters should be called only after the corresponding 
	// updateXXXIndex(...) is invoked
	///////////////////////////////////////////////////////////////////////////
	/**
	 * The following function must be called after {@updateIndex} for a new
	 * input 'buffer'. 
	 * 
	 * @param buffer
	 * @return
	 */
//	public Object getValue(ByteBuffer buffer, T colEnum) {				
//		//3. return the value
//		int[] pair = fieldIndex.get(colEnum);
//		Object ret = colEnum.getAttribute().getType().getValue(buffer, pair[0], pair[1]);
//		return ret == null ? colEnum.getAttribute().getDefaultValue() : ret;
//	}
//	
//	public List<Object> getCompressedColumnValues(ByteBuffer buffer, T colEnum) {
//		if (values.containsKey(colEnum)) {
//			return values.get(colEnum);
//		}
//		int rowNum = buffer.getInt(2);
//		
//		List<Object> ret = new ArrayList<Object>();
//		int offset = -1; 
//		if (fieldIndex.containsKey(colEnum)) {
//			offset = fieldIndex.get(colEnum)[0];
//		}
//		
//		if (offset > 0) {
//			ret = CompressScheme.getValueList(buffer, colEnum.getAttribute().getType(), 
//				rowNum, offset);
//		}
//		values.put(colEnum, ret);
//		return ret;
//	}
}
