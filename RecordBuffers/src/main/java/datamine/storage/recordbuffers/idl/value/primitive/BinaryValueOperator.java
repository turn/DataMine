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

final public class BinaryValueOperator extends AbstractPrimitiveValueOperator {

	public BinaryValueOperator() {
		super(-1);
	}

	@SuppressWarnings("unused")
	@Override
	public boolean isValid(Object value) {
		try {
			byte[] bVal = (byte[]) value;
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
			return (byte[]) value;
		}
	}

	@Override
	public Object getValue(ByteBuffer buf, int index, int length) {
		if (index >= 0 && length > 0) {
			byte[] bVal = new byte[length];
			System.arraycopy(buf.array(), index, bVal, 0, length);
			return bVal;
		} 
		return null;
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
	public int getMetadataSize() {
		return 4;
	}
}
