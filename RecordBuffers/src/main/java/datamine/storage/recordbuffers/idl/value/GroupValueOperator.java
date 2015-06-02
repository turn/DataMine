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
package datamine.storage.recordbuffers.idl.value;

import java.nio.ByteBuffer;

import datamine.storage.idl.FieldValueOperatorInterface;
import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.recordbuffers.Record;
import datamine.storage.recordbuffers.RecordBuffer;
import datamine.storage.recordbuffers.WritableRecord;

/**
 * @author yqi
 * @date Mar 26, 2015
 */
class GroupValueOperator implements FieldValueOperatorInterface {

	private final GroupFieldType type;
	public GroupValueOperator(GroupFieldType type) {
		this.type = type;
	}
	
	@Override
	public boolean isValid(Object val) {
		try {
			@SuppressWarnings({ "unused", "rawtypes" })
			Record buf = (Record) val;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	@Override
	public byte[] getByteArray(Object value) {
		if (value == null) { 
			return new byte[0];
		} else {
			@SuppressWarnings("rawtypes")
			Record record = (Record) value;
			return record.array();
		}
	}

	/**
	 * A new record buffer is created to contain all values from the input byte array,
	 * therefore the cost of memory allocation and value copying should be considered. 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getValue(ByteBuffer buffer, int index, int length) {
		// note that the length must be positive
		RecordBuffer rb = new RecordBuffer(buffer.array(), index, length);
		if (type.getTableClass() == null) {
			throw new IllegalArgumentException(type.getGroupName() + ": A valid group type should have its table class");
		}
		return new WritableRecord(type.getTableClass(), rb);
	}

	@Override
	public boolean hasFixedLength() {
		return false;
	}

	@Override
	public int getNumOfBytes(Object val) {
		if (val == null) {
			return 0;
		} else {
			@SuppressWarnings("rawtypes")
			Record record = (Record) val;
			return record.getNumOfBytes();
		}
	}

	@Override
	public int getMetadataLength() {
		return 4;
	}

}
