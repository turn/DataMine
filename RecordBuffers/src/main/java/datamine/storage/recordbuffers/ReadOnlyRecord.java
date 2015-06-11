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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * It defines a read-only record. In other words, writing/updating is not allowed. 
 * 
 * @author yqi
 */
public class ReadOnlyRecord<T extends Enum<T> & RecordMetadataInterface> extends Record<T> {

	public static final Logger LOG = LoggerFactory.getLogger(ReadOnlyRecord.class);
	
	/**
	 * An array of integers to store the offset of attribute values
	 * 
	 * <p>
	 * Any attribute/field with valid value has an offset in the array.   
	 * </p>
	 */
	private int[] offsetArray = null;
	
	/**
	 * The number of bytes used by the the instance of {@link RecordBuffer}.
	 */
	private int numOfBytes = 0;
	
	public ReadOnlyRecord(Class<T> clazz) {
		super(clazz);
	}
	
	public ReadOnlyRecord(Class<T> clazz, RecordBuffer buf) {
		super(clazz, buf);
		this.numOfBytes = buf.getRecordBufferSize();
	}

	/**
	 * Update the value of the input field
	 * 
	 * @param col the field of interest
	 * @param val the new value of the concerned field
	 */
	@Override
	public void setValue(T col, Object val) {
		throw new IllegalAccessError("No change is allowed for the read-only record!");
	}

	private int getOffset(T col) {
		Field field = col.getField();

		// when the column has its offset in the reference section
		FieldType type = field.getType();
		if ((field.isSortKey() || field.isFrequentlyUsed() || type instanceof CollectionFieldType) 
				&& offsetArray == null && this.buffer.getRecordBufferSize() > 0) {
			// find out the offset directly
			if (field.isSortKey()) {
				return this.meta.getSortKeyOffset(buffer);
			}
				
			if (field.isFrequentlyUsed()) {
				return this.meta.getFieldWithReferenceOffset(col, buffer);
			}
					
			// a collection-type field
			return this.meta.getCollectionOffset(col, buffer);
		}
		
		// other fields
		if (offsetArray == null) {
			initOffsetArray();
		}

		int id = field.getId() - 1; // note that id starts at 1.
		return offsetArray != null && offsetArray.length > id ? 				
				offsetArray[id] : -1;
	}
	
	@Override
	public Object getValue(T col) {
	
		long ts = 0;
		if (IS_DEBUG_ENABLED) {
			if (recursionLevel == 0) {
				ts = System.currentTimeMillis();
			}
			recursionLevel++;
		}
		
		Field field = col.getField();
		FieldType type = field.getType();
		FieldValueOperatorInterface valueOpr = FieldValueOperatorFactory.getOperator(type);
		ByteBuffer buf = buffer.getByteBuffer();
		Object result = null;
		int offset = getOffset(col);
		if (offset > 0 && type instanceof PrimitiveFieldType) {
			PrimitiveType priType = ((PrimitiveFieldType) type).getType();
			switch (priType) {
			case STRING:
				result = valueOpr.getValue(buf, offset + 2, buf.getShort(offset));
				break;
			case BINARY:
				result = valueOpr.getValue(buf, offset + 4, buf.getInt(offset));
				break;
			default:
				result = valueOpr.getValue(buf, offset, valueOpr.getNumOfBytes(null));
			}
		} else if (offset > 0 && (type instanceof GroupFieldType || type instanceof CollectionFieldType)) {
			result = valueOpr.getValue(buf, offset + 4, buf.getInt(offset));
		} 
		
		if (IS_DEBUG_ENABLED) {
			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
		
		// never return NULL
		if (result == null) { 
			return col.getField().getDefaultValue();
		} else {
			return result;
		}
	}
	
	public boolean getBool(T col) {
		
		long ts = 0;
		if (IS_DEBUG_ENABLED) {
			if (recursionLevel == 0) {
				ts = System.currentTimeMillis();
			}
			recursionLevel++;
		}
		
		int offset = getOffset(col);
		boolean result;
		if (offset > 0) {
			result = buffer.getByteBuffer().get(offset) == 1 ? true : false;
		} else {
			result = (Boolean) col.getField().getDefaultValue();
		}
		
		if (IS_DEBUG_ENABLED) {
			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
		
		return result;
	}

	
	public byte getByte(T col) {
		long ts = 0;
		if (IS_DEBUG_ENABLED) {
			if (recursionLevel == 0) {
				ts = System.currentTimeMillis();
			}
			recursionLevel++;
		}
		
		int offset = getOffset(col);
		byte result;
		if (offset > 0) {
			result = buffer.getByteBuffer().get(offset);
		} else {
			result = (Byte) col.getField().getDefaultValue();
		}
		
		if (IS_DEBUG_ENABLED) {
			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
		
		return result;
	}

	
	public short getShort(T col) {
		long ts = 0;
		if (IS_DEBUG_ENABLED) {
			if (recursionLevel == 0) {
				ts = System.currentTimeMillis();
			}
			recursionLevel++;
		}
		
		int offset = getOffset(col);
		short result;
		if (offset > 0) {
			result = buffer.getByteBuffer().getShort(offset);
		} else {
			result = (Short) col.getField().getDefaultValue();
		}
		
		if (IS_DEBUG_ENABLED) {
			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
		
		return result;
	}

	
	public long getLong(T col) {
		long ts = 0;
		if (IS_DEBUG_ENABLED) {
			if (recursionLevel == 0) {
				ts = System.currentTimeMillis();
			}
			recursionLevel++;
		}
		
		int offset = getOffset(col);
		long result;
		if (offset > 0) {
			result = buffer.getByteBuffer().getLong(offset);
		} else {
			result = (Long) col.getField().getDefaultValue();
		}
		
		if (IS_DEBUG_ENABLED) {
			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
		
		return result;
	}

	
	public int getInt(T col) {
		long ts = 0;
		if (IS_DEBUG_ENABLED) {
			if (recursionLevel == 0) {
				ts = System.currentTimeMillis();
			}
			recursionLevel++;
		}
		
		int offset = getOffset(col);
		int result;
		if (offset > 0) {
			result = buffer.getByteBuffer().getInt(offset);
		} else {
			result = (Integer) col.getField().getDefaultValue();
		}
		
		if (IS_DEBUG_ENABLED) {
			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
		
		return result;
	}

	
	public float getFloat(T col) {
		long ts = 0;
		if (IS_DEBUG_ENABLED) {
			if (recursionLevel == 0) {
				ts = System.currentTimeMillis();
			}
			recursionLevel++;
		}
		
		int offset = getOffset(col);
		float result;
		if (offset > 0) {
			result = buffer.getByteBuffer().getFloat(offset);
		} else {
			result = (Float) col.getField().getDefaultValue();
		}
		
		if (IS_DEBUG_ENABLED) {
			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
		
		return result;
	}

	
	public double getDouble(T col) {
		long ts = 0;
		if (IS_DEBUG_ENABLED) {
			if (recursionLevel == 0) {
				ts = System.currentTimeMillis();
			}
			recursionLevel++;
		}
		
		int offset = getOffset(col);
		double result;
		if (offset > 0) {
			result = buffer.getByteBuffer().getDouble(offset);
		} else {
			result = (Double) col.getField().getDefaultValue();
		}
		
		if (IS_DEBUG_ENABLED) {
			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
		
		return result;
	}

	
	public byte[] getBinary(T col) {
		long ts = 0;
		if (IS_DEBUG_ENABLED) {
			if (recursionLevel == 0) {
				ts = System.currentTimeMillis();
			}
			recursionLevel++;
		}
		
		int offset = getOffset(col);
		byte[] result;
		if (offset > 0) {
			int length = buffer.getByteBuffer().getInt(offset);
			byte[] out = new byte[length];
			byte[] src = buffer.getByteBuffer().array();
			System.arraycopy(src, offset + 4, out, 0, length);
			result = out;
		} else {
			result = (byte[]) col.getField().getDefaultValue();
		}
		
		if (IS_DEBUG_ENABLED) {
			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
		
		return result;
	}

	
	public String getString(T col) {
		long ts = 0;
		if (IS_DEBUG_ENABLED) {
			if (recursionLevel == 0) {
				ts = System.currentTimeMillis();
			}
			recursionLevel++;
		}
		
		int offset = getOffset(col);
		String result;
		if (offset > 0) {
			int length = buffer.getByteBuffer().getShort(offset);
			result = new String(buffer.getByteBuffer().array(), offset + 2, length);
		} else {
			result = (String) col.getField().getDefaultValue();
		}
		
		if (IS_DEBUG_ENABLED) {
			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
		
		return result;
	}
	
	/**
	 * Get the record buffer containing the record.
	 * @return the record buffer containing the record
	 */
	@Override
	public RecordBuffer getRecordBuffer() {
		return buffer;
	}

	@Override
	public int getNumOfBytes() {
		return this.numOfBytes;
	}

	@Override
	public byte[] array() {
		// first create the record buffer if necessary
		getRecordBuffer();
		if (buffer != null) {
			return buffer.getByteBuffer().array();
		} else {
			return new byte[0];
		}
	}

	@Override
	public int getListSize(T col) {
		//1. the input must be a collection-type field
		Preconditions.checkArgument(
				col.getField().getType() instanceof CollectionFieldType);
		
		//2. read the size directly from the byte array with the offset
		return this.meta.getCollectionSize(col, this.buffer);
	}
	
	
	/**
	 * Initiate the offset array for all valid values in the record buffer.
	 */
	private void initOffsetArray() {
		if (buffer != null && this.buffer.getRecordBufferSize() > 0) {
			constructOffsetArrayFromRecordBuffer();
		} 
	}
	
	/**
	 * It creates an array of offsets from the instance of {@link RecordBuffer}. 
	 */
	private void constructOffsetArrayFromRecordBuffer() {

		int initOffset = 0;
		ByteBuffer bytebuffer = this.buffer.getByteBuffer();
		List<T> fieldList = this.meta.getFieldList();
		
		//1. get the index of all fixed-length columns
		short length = bytebuffer.getShort(initOffset + 2); // the number of attributes
		short refSectionLength = bytebuffer.getShort(initOffset + 4);
		int posOfAttrs = refSectionLength + 6 + initOffset;
	
		offsetArray = new int[length];

		int offset = posOfAttrs + (length + 7) / 8; // skip # of attrs, flags;
		for (int i = 0; i < length; i++) {
			byte flag = bytebuffer.get(posOfAttrs + i / 8); 
			byte bit = (byte) (1 << ((7 - (i % 8))));
			T curField = fieldList.get(i); 
			if ((flag & bit) == 0) {
				offsetArray[i] = offset;
						
				FieldType fieldType = curField.getField().getType();
				FieldValueOperatorInterface valueOpr = FieldValueOperatorFactory.getOperator(fieldType);
				if (fieldType instanceof PrimitiveFieldType) {
					switch (((PrimitiveFieldType) fieldType).getType()) {
					case STRING:
						// string requires a SHORT to store its length (max < 32k)
						short arrayLength = bytebuffer.getShort(offset);
						if (arrayLength > 0) {
							offset += 2 + arrayLength;	
						}
						break;
					case BINARY:
						// other types require a INT to store its length info
						int arrayLength2 = bytebuffer.getInt(offset);
						if (arrayLength2 > 0) {
							offset += 4 + arrayLength2;	
						}
						break;
					default:// all other types with the fixed length
						int size = valueOpr.getNumOfBytes(null);
						offset += size;
						break;
					}
				} else if (fieldType instanceof GroupFieldType) {
					int arrayLength2 = bytebuffer.getInt(offset);
					if (arrayLength2 > 0) {
						offset += 4 + arrayLength2;	
					}
				} else if (fieldType instanceof CollectionFieldType) {
					int arrayLength2 = bytebuffer.getInt(offset);
					if (arrayLength2 > 0) {
						offset += 4 + arrayLength2;	
					}
				}
			} else {
				this.offsetArray[i] = -1;
			}
		}	
		this.numOfBytes = offset - initOffset;
	}
}
