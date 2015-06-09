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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
 * @author yqi
 */
public class WritableRecord<T extends Enum<T> & RecordMetadataInterface> extends Record<T> {

	public static final Logger LOG = LoggerFactory.getLogger(WritableRecord.class);
	
	/**
	 * To improve writing performance, the buffer is kept when the length
	 * of the buffer is smaller the following threshold.
	 */
	private final int RESERVE_BUFFER_CAP = 100 * 1000 * 1000; // 100M

	/**
	 * A flag to indicate if any field has been updated
	 */
	private boolean hasNewValues = false;
	
	/**
	 * An array of objects to store the attribute values
	 * 
	 * <p>
	 * Any attribute/field with valid value has an object in the array. Any update
	 * would be done to the corresponding slot in the array.   
	 * </p>
	 */
	private Object[] valueArray = null;
	
	/**
	 * The number of bytes used by the the instance of {@link RecordBuffer}.
	 */
	private int numOfBytes = 0;
	
	/**
	 * Handling reading if no writing is necessary
	 */
	private ReadOnlyRecord<T> readOnlyRecord = null;
	
	public WritableRecord(Class<T> clazz) {
		super(clazz);
	}
	
	public WritableRecord(Class<T> clazz, RecordBuffer buf) {
		super(clazz, buf);
		this.numOfBytes = buf.getRecordBufferSize();
		readOnlyRecord = new ReadOnlyRecord<T>(clazz, buf);
	}

	/**
	 * Update the value of the input field
	 * 
	 * TODO (Yan) support the deletion by making col null.
	 * 
	 * @param col the field of interest
	 * @param val the new value of the concerned field
	 */
	@Override
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

			if (valueArray[id] != null) {
				size = valOpr.getNumOfBytes(valueArray[id]);				
			} else {
				// add the overhead of the value
				this.numOfBytes += valOpr.getMetadataLength();
			}
			//3. update the value in the intermediate structure
			valueArray[id] = val;
			
			//4. determine the difference of sizes for serialization
			this.numOfBytes += valOpr.getNumOfBytes(val) - size;
			hasNewValues = true;
			if (this.buffer != null) {
				this.buffer.clear();
				this.buffer = null;
			}
		}
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
		
		if (valueArray == null && readOnlyRecord == null && buffer != null) {
			readOnlyRecord = new ReadOnlyRecord<T>(meta.getTableEnumClass(), buffer);
		}
		
		if (readOnlyRecord != null) {
			return readOnlyRecord.getValue(col);
		}
				
		Field field = col.getField();
		int id = field.getId() - 1; // note that id starts at 1.
 		Object result = valueArray != null && valueArray.length > id ? valueArray[id] : null;
 		
 		if (IS_DEBUG_ENABLED) {
 			recursionLevel--;
			if (recursionLevel == 0) {
				readingTimeCostMiliSeconds += System.currentTimeMillis() - ts;
			} 
		}
 		
		if (result == null) { // never return NULL
			return col.getField().getDefaultValue();
		} else {
			return result;
		}
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
	@Override
	public RecordBuffer getRecordBuffer() {
		if (valueArray != null && hasNewValues) {
			// in case the buffer is not cleared yet
			if (this.buffer != null) {
				this.buffer.clear();
			}
			
			// create the record buffer from the object array
			constructRecordBufferFromValueArray();
			
			// clean up the flag and the object array
			this.valueArray = null;
			this.hasNewValues = false;
		} 
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

	@SuppressWarnings("rawtypes")
	@Override
	public int getListSize(T col) {
		//1. the input must be a collection-type field
		Preconditions.checkArgument(
				col.getField().getType() instanceof CollectionFieldType);
		//2. read the size directly from the byte array with the offset
		if (valueArray == null) {
			return this.meta.getCollectionSize(col, this.buffer);
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
	 * Initiate the value array for all valid values in the record buffer.
	 * 
	 * <p>
	 * It happens with any updates for the current record. 
	 * </p>
	 */
	private void initValueArray() {
		// either readOnlyRecord or valueArray must be null
		readOnlyRecord = null;
		if (buffer != null && this.buffer.getRecordBufferSize() > 0) {
			constructValueArrayFromRecordBuffer();
			if (buffer.getRecordBufferSize() > RESERVE_BUFFER_CAP) {
				this.buffer.clear();
				this.buffer = null;
				this.hasNewValues = true; // Fake for {@getRecordBuffer()}
			}
		} else {
			if (this.valueArray == null) {
				this.valueArray = new Object[this.meta.getTableSize()];
				this.numOfBytes = this.meta.getMaxHeaderLength();
			}
		}
	}
	
	/**
	 * It creates an array of objects from the instance of {@link RecordBuffer}. 
	 */
	private void constructValueArrayFromRecordBuffer() {

		int initOffset = 0;
		ByteBuffer bytebuffer = this.buffer.getByteBuffer();
		List<T> fieldList = this.meta.getFieldList();
		
		//1. get the index of all fixed-length columns
		@SuppressWarnings("unused")
		short version = bytebuffer.getShort(initOffset);
		short length = bytebuffer.getShort(initOffset + 2); // the number of attributes
		short refSectionLength = bytebuffer.getShort(initOffset + 4);
		int posOfAttrs = refSectionLength + 6 + initOffset;
	
		valueArray = new Object[length];

		int offset = posOfAttrs + (length + 7) / 8; // skip # of attrs, flags;
		for (int i = 0; i < length; i++) {
			byte flag = bytebuffer.get(posOfAttrs + i / 8); 
			byte bit = (byte) (1 << ((7 - (i % 8))));
			T curField = fieldList.get(i); 
			if ((flag & bit) == 0) {

				FieldType fieldType = curField.getField().getType();
				int id = curField.getField().getId() - 1; // note that the ID starts at 1 instead of 0
				FieldValueOperatorInterface valueOpr = FieldValueOperatorFactory.getOperator(fieldType);
				if (fieldType instanceof PrimitiveFieldType) {
					switch (((PrimitiveFieldType) fieldType).getType()) {
					case STRING:
						// string requires a SHORT to store its length (max < 32k)
						short arrayLength = bytebuffer.getShort(offset);
						if (arrayLength >= 0) {
							valueArray[id] = valueOpr.getValue(bytebuffer, offset + 2, arrayLength);
							offset += 2 + arrayLength;	
						}
						break;
					case BINARY:
						// other types require a INT to store its length info
						int arrayLength2 = bytebuffer.getInt(offset);
						if (arrayLength2 >= 0) {
							valueArray[id] = valueOpr.getValue(bytebuffer, offset + 4, arrayLength2);
							offset += 4 + arrayLength2;	
						}
						break;
					default:// all other types with the fixed length
						int size = valueOpr.getNumOfBytes(null);
						valueArray[id] = valueOpr.getValue(bytebuffer, offset, size);
						offset += size;
						break;
					}
				} else if (fieldType instanceof GroupFieldType) {
					int arrayLength2 = bytebuffer.getInt(offset);
					if (arrayLength2 > 0) {
						valueArray[id] = valueOpr.getValue(bytebuffer, offset + 4, arrayLength2);
						offset += 4 + arrayLength2;	
					}
				} else if (fieldType instanceof CollectionFieldType) {
					int arrayLength2 = bytebuffer.getInt(offset);
					if (arrayLength2 > 0) {
						valueArray[id] = valueOpr.getValue(bytebuffer, offset + 4, arrayLength2);
						offset += 4 + arrayLength2;	
					}
				}
				 
				
			} 
		}	
		this.numOfBytes = offset - initOffset;
	}

	/**
	 * Create a byte buffer containing the input record and the input objects
	 * would be removed at the same time. 
	 * 
	 * @param input an array of attributes from a record.
	 * @return a byte buffer containing the input record.
	 */
	private void constructRecordBufferFromValueArray() {
		
		List<T> fieldList = this.meta.getFieldList();
		
		//0. prepare for the output
		byte[] out = new byte[0];
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			//1. store the attributes in fixed length
			short numOfAttr = (short) fieldList.size();
			short version = fieldList.get(0).getVersion();

			//2. store bit flags next to indicate empty columns;
			byte[] flags = new byte[(numOfAttr + 7) / 8];

			//3. fill the first 6 bytes
			int curPosition = 0;
			outStream.write(ByteBuffer.allocate(2).putShort(version).array());
			curPosition += 2;
			outStream.write(ByteBuffer.allocate(2).putShort(numOfAttr).array());
			curPosition += 2;
			outStream.write(ByteBuffer.allocate(2).putShort(this.meta.getReferenceSectionLength()).array());
			curPosition += 2;

			//4. prepare for the sorted key reference
			int posOfsortedKey = -1;
			if (this.meta.hasSortedKey()) {
				posOfsortedKey = curPosition;
				outStream.write(new byte[]{(byte)0,(byte)0,(byte)0,(byte)0});
				curPosition += 4;
			}

			//5. prepare for the references to the list type fields
			int posOfCollectionTypeReferenceFields = -1;
			ByteBuffer colFieldOffsetListByteBuf = null;
			if (this.meta.hasCollectionFieldInReferenceSection()) {
				byte numOfCollections = this.meta.getNumOfCollectionFieldInReferenceSection();
				colFieldOffsetListByteBuf = ByteBuffer.allocate(4 * numOfCollections);
				outStream.write(numOfCollections);
				curPosition += 1;
				posOfCollectionTypeReferenceFields = curPosition;
				for (int i = 0; i < numOfCollections; i++) {
					outStream.write(new byte[]{(byte)0,(byte)0,(byte)0,(byte)0});
					curPosition += 4;
					colFieldOffsetListByteBuf.putInt(-1);
				}
			} else {
				outStream.write(0);
				curPosition += 1;
			}

			//6. prepare for the references to the fields with reference
			int posOfFieldsWithReference = -1;
			ByteBuffer refFieldOffsetListByteBuf = null;
			if (this.meta.hasNonCollectionFieldInReferenceSection()) {
				byte numOfReferenceFields = this.meta.getNumOfNonCollectionFieldInReferenceSection();
				refFieldOffsetListByteBuf = ByteBuffer.allocate(6 * numOfReferenceFields);
				outStream.write(numOfReferenceFields);
				curPosition += 1;
				posOfFieldsWithReference = curPosition;
				for (int i = 0; i < numOfReferenceFields; ++i) {
					outStream.write(new byte[]{(byte)0,(byte)0});
					curPosition += 2;
					outStream.write(new byte[]{(byte)0,(byte)0,(byte)0,(byte)0});
					curPosition += 4;
				}
			} else {
				outStream.write(0);
				curPosition += 1;
			}

			//7. prepare the bit-maps
			int posOfFlags = curPosition; 
			outStream.write(flags);
			curPosition += flags.length;

			//8. start put the values into the byte array
			int sortKeyOffset = -1;
			for (int i = 0; i < numOfAttr; ++i) {

				boolean hasValidValue = false;

				// find a valid value when it is still smaller than ... 
				if (i < valueArray.length) {

					T curCol = fieldList.get(i);
					Object val = valueArray[i];
					Field curField = curCol.getField();
					FieldType curFieldType = curField.getType();
					FieldValueOperatorInterface valOpr = 
							FieldValueOperatorFactory.getOperator(curFieldType);

					if (val != null && valOpr.isValid(val) && 
							!curField.equalToDefaultValue(val)) {

						byte[] byteArray = valOpr.getByteArray(val);
						if (byteArray.length >= 0) { // a dummy '0' must be written even if it has no value

							hasValidValue = true;

							// if it is a sort key
							if (curField.isDesSortKey()) {
								sortKeyOffset = curPosition;
							}

							// if it is collection type field
							if (curFieldType instanceof CollectionFieldType) {
								int seqNo = this.meta.getSequenceOfCollectionField(
										curField.getId());
								colFieldOffsetListByteBuf.putInt(seqNo * 4, curPosition);
							}

							// if it is a field with reference 
							if (curField.isFrequentlyUsed()) {
								refFieldOffsetListByteBuf.putShort((short) curField.getId());
								refFieldOffsetListByteBuf.putInt(curPosition);
							}

							// as a common field
							if (valOpr.hasFixedLength()) {
								outStream.write(byteArray);
								curPosition += byteArray.length;
							} else {
								if (curFieldType instanceof PrimitiveFieldType && 
										((PrimitiveFieldType) curFieldType).getType().equals(PrimitiveType.STRING)) {
									if (byteArray.length > Short.MAX_VALUE) {
										outStream.write(new byte[]{(byte)0,(byte)0});
										curPosition += 2;
										LOG.error("A super-long value occurs to " + curCol);
									} else {
										short len = (short) byteArray.length;
										outStream.write(ByteBuffer.allocate(2).putShort(len).array());
										curPosition += 2;
										outStream.write(byteArray);
										curPosition += byteArray.length;
									}	
								} else {
									outStream.write(ByteBuffer.allocate(4).putInt(byteArray.length).array());
									curPosition += 4;
									outStream.write(byteArray);
									curPosition += byteArray.length;
								}
							}	
							// clean up the value in the input list
							valueArray[i] = null;
						}
					} 	
				}

				if (!hasValidValue) {
					// store flag: 1 for empty value
					byte bit = (byte) (1 << (7 - (i % 8)));
					byte flag = flags[i / 8];
					flags[i / 8] = (byte) (flag | bit);
				}
			}
			
			//9. prepare the byte array for the result
			out = outStream.toByteArray();
			outStream.close();
			outStream = null;
			
			//10. fill the blanks
			//10.1 flags
			System.arraycopy(flags, 0, out, posOfFlags, flags.length);
			
			//10.2 sort-key
			if (sortKeyOffset > 0) {
				System.arraycopy(ByteBuffer.allocate(4).putInt(sortKeyOffset).array(), 0, out, posOfsortedKey, 4);
			}
			
			//10.3 collection type field
			if (posOfCollectionTypeReferenceFields > 0) {
				byte[] offsetByteArray = colFieldOffsetListByteBuf.array();
				System.arraycopy(offsetByteArray, 0, out, posOfCollectionTypeReferenceFields, offsetByteArray.length);
			}
			
			//10.4 field with 'hasRef' annotation
			if (posOfFieldsWithReference > 0) {
				byte[] offsetByteArray = refFieldOffsetListByteBuf.array();
				System.arraycopy(offsetByteArray, 0, out, posOfFieldsWithReference, offsetByteArray.length);
			}
			
		} catch (IOException e) {
			LOG.error("Cannot open a stream to convert the input array to a byte array!");
		} 
		// update the the instance of {@link RecordBuffer}
		this.numOfBytes = out.length;
		this.buffer = new RecordBuffer();
		this.buffer.wrap(out, 0, out.length);
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	public boolean getBool(T col) {
		if (readOnlyRecord != null) {
			return readOnlyRecord.getBool(col);
		}
		return (Boolean) getValue(col);
	}

	
	public byte getByte(T col) {
		if (readOnlyRecord != null) {
			return readOnlyRecord.getByte(col);
		}
		return (Byte) getValue(col);
	}

	
	public short getShort(T col) {
		if (readOnlyRecord != null) {
			return readOnlyRecord.getShort(col);
		}
		return (Short) getValue(col);
	}

	
	public long getLong(T col) {
		if (readOnlyRecord != null) {
			return readOnlyRecord.getLong(col);
		}
		return (Long) getValue(col);
	}

	
	public int getInt(T col) {
		if (readOnlyRecord != null) {
			return readOnlyRecord.getInt(col);
		}
		return (Integer) getValue(col);
	}

	
	public float getFloat(T col) {
		if (readOnlyRecord != null) {
			return readOnlyRecord.getFloat(col);
		}
		return (Float) getValue(col);
	}

	
	public double getDouble(T col) {
		if (readOnlyRecord != null) {
			return readOnlyRecord.getDouble(col);
		}
		return (Double) getValue(col);
	}

	
	public byte[] getBinary(T col) {
		if (readOnlyRecord != null) {
			return readOnlyRecord.getBinary(col);
		}
		return (byte[]) getValue(col);
	}

	
	public String getString(T col) {
		if (readOnlyRecord != null) {
			return readOnlyRecord.getString(col);
		}
		return (String) getValue(col);
	}
}
