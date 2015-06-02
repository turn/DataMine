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
package datamine.storage.recordbuffers.idl.value.primitive;

import java.nio.ByteBuffer;

final public class StringValueOperator extends AbstractPrimitiveValueOperator {

	public StringValueOperator() {
		super(-1);
	}
	
	@Override
	public boolean isValid(Object value) {
		try {
			@SuppressWarnings("unused")
			String bVal = (String) value;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	@Override
	public byte[] getByteArray(Object value) {
		if (value == null) { // is it necessary?
			return new byte[0];
		} else {
			return ((String) value).getBytes();
		}
	}

	@Override
	public Object getValue(ByteBuffer  buf, int index, int length) {
		return getString(buf, index, length);
	}
	
	public String getString(ByteBuffer buffer, int index, int length) {
		if (index >= 0 && length >= 0) {
			return new String(buffer.array(), index, length);			
		} else {
			throw new IllegalArgumentException("The negative index/length : " 
					+ index + "/" + length);
		}
	}

	@Override
	public boolean hasFixedLength() {
		return false;
	}
	
	@Override
	public int getNumOfBytes(Object val) {
		return getByteArray(val).length;
	}
	
	@Override
	public int getMetadataLength() {
		return 2;
	}
}
