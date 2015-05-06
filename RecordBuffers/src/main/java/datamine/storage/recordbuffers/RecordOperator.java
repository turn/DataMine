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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

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
 * MetadataOperator defines the functions to read/write attributes of a
 * table defined by ENUM<T>.
 * 
 * @author yqi
 */
class RecordOperator<T extends Enum<T> & RecordMetadataInterface> {

	public static final Logger LOG = LoggerFactory.getLogger(RecordOperator.class);
	
	private final Class<T> dummyClass; // a helper to collect ENUM info
	private final List<T> fieldList; // a list of fields ordered by its 'ID'
	private final ReferenceSection refSection;

	// The factory pattern to minimize the instances of the class
	@SuppressWarnings("rawtypes")
	private static Map<Class, RecordOperator> operatorMap = Maps.newHashMap();
	
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T> & RecordMetadataInterface> RecordOperator<T> 
	getRecordOperator(Class<T> enumClass) {
		if (operatorMap.containsKey(enumClass)) {
			return operatorMap.get(enumClass);
		} else {
			RecordOperator<T> newElem = new RecordOperator<T>(enumClass);
			operatorMap.put(enumClass, newElem);
			return newElem;
		}
	}

	/**
	 * Private constructor: must create an instance from the static method (i.e.,
	 * getRecordOperator(...)
	 * 
	 * @param enumClass the ENUM defines the table schema
	 */
	private RecordOperator(Class<T> enumClass) {
		dummyClass = enumClass;
		fieldList = Arrays.asList(dummyClass.getEnumConstants());

		// sort the attr
		Collections.sort(fieldList,
				new AttributeComparator<T>());

		// build the reference section
		refSection = new ReferenceSection(fieldList); // 4 bytes for version # and # of attributes
	}

	/**
	 * Return the number of attributes defined in the table
	 * @return the number of attributes defined in the table
	 */
	public int getTableSize() {
		return this.fieldList.size();
	}

	/**
	 * Create a record having all values in the input object array
	 * 
	 * @param input an array of objects for attributes
	 * @return a record having all attributes in the input
	 */
	public RecordBuffer getRecordBuffer(Object[] input) {
		byte[] values = getByteArrayAndCleanInputs(input);
		RecordBuffer rb = new RecordBuffer();
		rb.wrap(values, 0, values.length);
		return rb;
	}

	/**
	 * Get the offset of the sort-key field in the record buffer
	 * @param buffer the byte buffer storing the record
	 * @param initOffset the starting position of the record
	 * @return the offset of the sort-key field in the record buffer
	 */
	public int getSortKeyOffset(ByteBuffer buffer, int initOffset) {
		if (refSection.hasSortKey) {
			// 2 bytes for the length of the reference section
			return buffer.getInt(initOffset + 4 + 2); 
		} else {
			return -1;
		}
	}

	/**
	 * TODO it may not be defined properly
	 * 
	 * @return An estimation of the size of header
	 */
	public int getMaxHeaderSize() {
		return 6 + refSection.length + (this.fieldList.size() + 7) / 8;
	}
	
	/**
	 * Get the offset of the field with 'hasRef' annotation.
	 * 
	 * @param col the field with 'hasRef' annotation
	 * @param buffer the byte buffer containing the record
	 * @param initOffset the starting position of the record in the byte buffer
	 * @return the offset of the field with 'hasRef' annotation.
	 */
	public int getFieldWithReferenceOffset(T col, ByteBuffer buffer, int initOffset) {
		Preconditions.checkArgument(col.getField().hasReference(), 
				col + " is not a field with the reference in the reference section!");

		int id = col.getField().getId();
		int length = buffer.getShort(initOffset + 4);
		if (length > 2) {

			int offset = initOffset +  
					4 + // version # + # of attributes
					2 + // length of reference section
					(refSection.hasSortKey ? 4 : 0);

			int numOfCollectionType = buffer.get(offset);
			offset += 1 + // the # of collection-type fields
					4 * numOfCollectionType;

			int refereceFieldNum = buffer.get(offset);
			offset += 1;
			for (int i = 0; i < refereceFieldNum; ++i) {
				if (id == buffer.getShort(offset)) {
					return buffer.getInt(offset + 2);
				}
				offset += 2 + 4;
			}
		}
		return -1;
	}

	
	/**
	 * Create a byte buffer containing the input record and the input objects
	 * would be removed at the same time. 
	 * 
	 * @param input an array of attributes from a record.
	 * @return a byte buffer containing the input record.
	 */
	public byte[] getByteArrayAndCleanInputs(Object[] input) {

		Preconditions.checkArgument(input.length > 1,
				"There should be at least one value in the input!");

		Preconditions.checkArgument(input.length - 1 <= fieldList.size(),
				"The # of attributes in the input is larger than table size! - " +
				input.length + " and " + fieldList.size());

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
			outStream.write(ByteBuffer.allocate(2).putShort(refSection.length).array());
			curPosition += 2;

			//4. prepare for the sorted key reference
			int posOfsortedKey = -1;
			if (refSection.hasSortKey) {
				posOfsortedKey = curPosition;
				outStream.write(new byte[]{(byte)0,(byte)0,(byte)0,(byte)0});
				curPosition += 4;
			}

			//5. prepare for the references to the list type fields
			int posOfCollectionTypeReferenceFields = -1;
			ByteBuffer colFieldOffsetListByteBuf = null;
			if (refSection.collectionReferenceSequenceMap != null) {
				byte numOfCollections = (byte) refSection.collectionReferenceSequenceMap.size();
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
			if (refSection.nonCollectionReferenceOffsetMap != null) {
				byte numOfReferenceFields = (byte) refSection.nonCollectionReferenceOffsetMap.size();
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
				if (i < input.length) {

					T curCol = fieldList.get(i);
					Object val = input[i];
					Field curField = curCol.getField();
					FieldType curFieldType = curField.getType();
					FieldValueOperatorInterface valOpr = 
							FieldValueOperatorFactory.getOperator(curFieldType);

					if (val != null && valOpr.isValid(val) && 
							!curField.equalToDefaultValue(val)) {

						byte[] byteArray = valOpr.getByteArray(val);
						if (byteArray.length > 0) {

							hasValidValue = true;

							// if it is a sort key
							if (curField.isDesSortKey()) {
								sortKeyOffset = curPosition;
							}

							// if it is collection type field
							if (curFieldType instanceof CollectionFieldType) {
								int seqNo = refSection.collectionReferenceSequenceMap.get(
										curField.getId());
								colFieldOffsetListByteBuf.putInt(seqNo * 4, curPosition);
							}

							// if it is a field with reference 
							if (curField.hasReference()) {
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
							input[i] = null;
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
		return out;
	}
	
	/**
	 * Read all objects from the input buffer. All objects are stored in 
	 * an array, where the first element is the number of bytes used entirely
	 * 
	 * @param buffer the byte buffer containing the record
	 * @param initOffset the initial offset (not smaller than 0)
	 * @return an array of objects in the input byte buffer.
	 */
	public Object[] getValueArray(ByteBuffer buffer, int initOffset) {

		Preconditions.checkArgument(initOffset >= 0);

		//1. get the index of all fixed-length columns
		@SuppressWarnings("unused")
		short version = buffer.getShort(initOffset);
		short length = buffer.getShort(initOffset + 2); // the number of attributes
		short refSectionLength = buffer.getShort(initOffset + 4);
		int posOfAttrs = refSectionLength + 6 + initOffset;
	
		Object[] valueArray = new Object[length+1];

		int offset = posOfAttrs + (length + 7) / 8; // skip # of attrs, flags;
		for (int i = 0; i < length; i++) {
			byte flag = buffer.get(posOfAttrs + i / 8); 
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
						short arrayLength = buffer.getShort(offset);
						if (arrayLength > 0) {
							valueArray[id] = valueOpr.getValue(buffer, offset + 2, arrayLength);
							offset += 2 + arrayLength;	
						}
						break;
					case BINARY:
						// other types require a INT to store its length info
						int arrayLength2 = buffer.getInt(offset);
						if (arrayLength2 > 0) {
							valueArray[id] = valueOpr.getValue(buffer, offset + 4, arrayLength2);
							offset += 4 + arrayLength2;	
						}
						break;
					default:// all other types with the fixed length
						int size = valueOpr.getNumOfBytes(null);
						valueArray[id] = valueOpr.getValue(buffer, offset, size);
						offset += size;
						break;
					}
				} else if (fieldType instanceof GroupFieldType) {
					int arrayLength2 = buffer.getInt(offset);
					if (arrayLength2 > 0) {
						valueArray[id] = valueOpr.getValue(buffer, offset + 4, arrayLength2);
						offset += 4 + arrayLength2;	
					}
				} else if (fieldType instanceof CollectionFieldType) {
					int arrayLength2 = buffer.getInt(offset);
					if (arrayLength2 > 0) {
						valueArray[id] = valueOpr.getValue(buffer, offset + 4, arrayLength2);
						offset += 4 + arrayLength2;	
					}
				}
				 
				
			} 
		}	
		valueArray[length] = offset - initOffset;
		return valueArray;		
	}

	/**
	 * It defines the data structure of reference section in the record
	 * buffer. 
	 * 
	 * <p>
	 * The reference section has the following parts:
	 * <i> Sort-key field reference (if exists) : 
	 * <i> Collection-type fields reference (if exists) :
	 * <i> Field with 'hasRef' annotation (if exists) : 
	 * </p>
	 * 
	 * 
	 * @author yqi
	 */
	public class ReferenceSection {
		// how many bytes used; 1 for collection type and 1 for the field with reference
		private short length = 2; 				
		// must decide at the creation; once set, no way to peel
		private boolean hasSortKey = false; 	
		// monotonic increasing
		private Map<Integer, Integer> collectionReferenceSequenceMap;  
		// can change from version to version
		private Map<Integer, Integer> nonCollectionReferenceOffsetMap; 

		public ReferenceSection(final List<T> fieldList) {
			boolean hasCollectionType = false;
			int sequenceNo = 0;
			boolean hasReferenceType = false;
			for (T cur : fieldList) {
				Field field = cur.getField();
				if (field.isDesSortKey()) {
					hasSortKey = true;
					length += 4; // 4 (or integer) for the reference to the pos of sorted key attribute
				}

				if (field.getType() instanceof CollectionFieldType) {
					if (!hasCollectionType) {
						hasCollectionType = true;
						collectionReferenceSequenceMap = Maps.newHashMap();
					}
					collectionReferenceSequenceMap.put(field.getId(), sequenceNo++);
					length += 4;
				} else if (field.hasReference()) {
					// A collection-type field should not have "REFERENCE"
					if (!hasReferenceType) {
						hasReferenceType = true;
						nonCollectionReferenceOffsetMap = Maps.newHashMap();
					}
					// 2 bytes for the field ID
					nonCollectionReferenceOffsetMap.put(field.getId(), length + 2); 
					length += 2 + 4; // additional 4 bytes for the reference 
				}
			}
		}
	}

	/**
	 * We want to keep the order of field list in every scenario
	 * 
	 * @author yqi
	 * @date Jan 9, 2015
	 */
	public static class AttributeComparator<T extends Enum<T> & RecordMetadataInterface> 
	implements Comparator<T> {

		@Override
		public int compare(T o1, T o2) {
			return o1.getField().getId() - o2.getField().getId();
		}
	}
}
