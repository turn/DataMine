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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Field;
import datamine.storage.idl.type.CollectionFieldType;

/**
 * RecordBufferMeta stores the information required for the serialization 
 * of a table defined by ENUM<T>. For example a header is needed when representing
 * a table record with the {@link RecordBuffer} instance. 
 * 
 * @author yqi
 */
public class RecordBufferMeta<T extends Enum<T> & RecordMetadataInterface> {

	public static final Logger LOG = LoggerFactory.getLogger(RecordBufferMeta.class);
	
	private final Class<T> dummyClass; // a helper to collect ENUM info
	private final List<T> fieldList; // a list of fields ordered by its 'ID'
	private final Map<String, T> nameFieldMap;
	private final ReferenceSection refSection;

	// The factory pattern to minimize the instances of the class
	@SuppressWarnings("rawtypes")
	private static Map<Class, RecordBufferMeta> operatorMap = Maps.newHashMap();
	
	@SuppressWarnings("unchecked")
	public static <T extends Enum<T> & RecordMetadataInterface> RecordBufferMeta<T> 
		getRecordOperator(Class<T> enumClass) {
		if (operatorMap.containsKey(enumClass)) {
			return operatorMap.get(enumClass);
		} else {
			RecordBufferMeta<T> newElem = new RecordBufferMeta<T>(enumClass);
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
	private RecordBufferMeta(Class<T> enumClass) {
		dummyClass = enumClass;
		fieldList = Lists.newArrayList();
		nameFieldMap = Maps.newHashMap();
		// the derived field should be ignored 
		for (T cur : dummyClass.getEnumConstants()) {
			Field field = cur.getField();
			if (field.getId() > 0) {
				fieldList.add(cur);
				nameFieldMap.put(field.getName(), cur);
			}
		}

		// sort the field list
		Collections.sort(fieldList,
				new AttributeComparator<T>());

		// build the reference section
		refSection = new ReferenceSection(fieldList); // 4 bytes for version # and # of attributes
	}

	/**
	 * Find the field (i.e., the corresponding ENUM) given a name
	 * 
	 * @param name the field name
	 * @return the field ENUM, or null if the field doesn't exist
	 */
	public T getField(String name) {
		return nameFieldMap.get(name);
	}
	
	/**
	 * @return the ENUM defines the table schema
	 */
	public Class<T> getTableEnumClass() {
		return dummyClass;
	}
	
	/**
	 * @return the name of the table
	 */
	public String getTableName() {
		return fieldList.get(0).getTableName();
	}
	
	/**
	 * Return the number of attributes defined in the table
	 * @return the number of attributes defined in the table
	 */
	public int getTableSize() {
		return this.fieldList.size();
	}

	/**
	 * TODO it may not be defined properly
	 * 
	 * @return An estimation of the number of bytes consumed as a header
	 */
	public int getMaxHeaderLength() {
		return 6 + refSection.length + (this.fieldList.size() + 7) / 8;
	}
	
	/**
	 * @return A list of fields in the table.
	 */
	public List<T> getFieldList() {
		return this.fieldList;
	}
	
	/**
	 * @return true if there is a field as the sort-key
	 */
	public boolean hasSortedKey() {
		return this.refSection.hasSortKey;
	}
	
	/**
	 * @return the number of bytes used by the reference section in the instance 
	 * of {@link RecordBuffer}
	 */
	public short getReferenceSectionLength() {
		return this.refSection.length;
	}
	
	/**
	 * @return true if there is any collection-type field in the record
	 */
	public boolean hasCollectionFieldInReferenceSection() {
		return this.refSection.collectionReferenceSequenceMap != null;
	}
	
	/**
	 * @return true if there is any non-collection-type field in the reference section.
	 */
	public boolean hasNonCollectionFieldInReferenceSection() {
		return this.refSection.nonCollectionReferenceOffsetMap != null;
	}
	
	/**
	 * @return the number of collection-type fields in the record
	 */
	public byte getNumOfCollectionFieldInReferenceSection() {
		return (byte) (hasCollectionFieldInReferenceSection() 
				? this.refSection.collectionReferenceSequenceMap.size() : 0);
	}
	
	/**
	 * @return the number of non-collection-type fields in the reference section
	 */
	public byte getNumOfNonCollectionFieldInReferenceSection() {
		return (byte) (hasNonCollectionFieldInReferenceSection() 
				? this.refSection.nonCollectionReferenceOffsetMap.size() : 0);
	}
	
	/**
	 * By default an order is applied to all collection-type fields. The function 
	 * gets the sequence id (starting at 0) of the input field. 
	 * 
	 * <p>
	 * If the input is not a collection-type field, -1 would be returned. 
	 * </p>
	 * 
	 * @param fieldId the field ID of the input collection-type attribute
	 * @return the sequence id (starting at 0) of the input collection-type field
	 */
	public int getSequenceOfCollectionField(int fieldId) {
		return hasCollectionFieldInReferenceSection() 
				? this.refSection.collectionReferenceSequenceMap.get(fieldId) : -1;
	}
	
	/** 
	 * Find out the size of the collection-type field in the record
	 * @param col the collection-type field 
	 * @param buffer the record buffer storing the record
	 * @return the size of the collection-type field in the record
	 */
	public int getCollectionSize(T col, RecordBuffer rb) {
		return getCollectionSize(col.getField(), rb);
	}

	/** 
	 * Find out the size of the collection-type field in the record
	 * @param field the collection-type field 
	 * @param buffer the record buffer storing the record
	 * @return the size of the collection-type field in the record
	 */
	public int getCollectionSize(Field field, RecordBuffer rb) {
		int offset = getCollectionOffset(field, rb);
		if (offset > 0) {
			return rb.getByteBuffer().getInt(offset + 4);
		} else {
			return 0;
		}
	}
	
	/**
	 * Find out the offset of the input collection-type field
	 * @param col the collection-type field
	 * @param rb the record buffer storing the record
	 * @return the offset of the input field in the record buffer
	 */
	public int getCollectionOffset(T col, RecordBuffer rb) {
		return getCollectionOffset(col.getField(), rb);
	}
	
	/**
	 * Find out the offset of the input collection-type field
	 * @param field the collection-type field
	 * @param rb the record buffer storing the record
	 * @return the offset of the input field in the record buffer
	 */
	public int getCollectionOffset(Field field, RecordBuffer rb) {
		ByteBuffer byteBuffer = rb.getByteBuffer();
		int initOffset = 0;
		int id = field.getId();
		int seqenceNo = this.getSequenceOfCollectionField(id);
		if (seqenceNo >= 0) {
			int offset = initOffset + 4 + 2 + (this.hasSortedKey() ? 4 : 0);
			int numOfCollectionsInRecord = byteBuffer.get(offset);
			offset += 1; // for the number of collection-type fields

			if (seqenceNo < numOfCollectionsInRecord) {
				offset += 4 * seqenceNo; // the position of the reference
				return byteBuffer.getInt(offset);
			} 
		}
		return -1;
	}
	
	/**
	 * Get the offset of the field with 'hasRef' annotation.
	 * 
	 * @param col the field with 'hasRef' annotation
	 * @return the offset of the field with 'hasRef' annotation.
	 */
	public int getFieldWithReferenceOffset(T col, RecordBuffer rb) {
		return getFieldWithReferenceOffset(col.getField(), rb);
	}
	
	/**
	 * Get the offset of the field with 'hasRef' annotation.
	 * 
	 * @param field the field with 'hasRef' annotation
	 * @return the offset of the field with 'hasRef' annotation.
	 */
	public int getFieldWithReferenceOffset(Field field, RecordBuffer rb) {
		
		ByteBuffer byteBuffer = rb.getByteBuffer();
		int initOffset = 0;
		
		int id = field.getId();
		int length = byteBuffer.getShort(initOffset + 4);
		if (length > 2) {

			int offset = initOffset +  
					4 + // version # + # of attributes
					2 + // length of reference section
					(this.hasSortedKey() ? 4 : 0);

			int numOfCollectionType = byteBuffer.get(offset);
			offset += 1 + // the # of collection-type fields
					4 * numOfCollectionType;

			int refereceFieldNum = byteBuffer.get(offset);
			offset += 1;
			for (int i = 0; i < refereceFieldNum; ++i) {
				if (id == byteBuffer.getShort(offset)) {
					return byteBuffer.getInt(offset + 2);
				}
				offset += 2 + 4;
			}
		}
		return -1;
	}
	
	/**
	 * Get the offset of the sort-key field in the record buffer
	 * @param buffer the byte buffer storing the record
	 * @param initOffset the starting position of the record
	 * @return the offset of the sort-key field in the record buffer
	 */
	public int getSortKeyOffset(RecordBuffer rb) {
		if (this.hasSortedKey()) {
			// 2 bytes for the length of the reference section
			return rb.getByteBuffer().getInt(4 + 2); 
		} else {
			return -1;
		}
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
	 * <p>
	 * It is OK to keep it non-static as the number of {@RecordBufferMeta} 
	 * instances is not larger than that of tables
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
				} else if (field.isFrequentlyUsed()) {
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
