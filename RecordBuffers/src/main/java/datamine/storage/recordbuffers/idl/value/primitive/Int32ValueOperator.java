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

final public class Int32ValueOperator extends AbstractPrimitiveValueOperator {

	public Int32ValueOperator() {
		super(4);
	}

	@Override
	public boolean isValid(Object value) {
		try {
			@SuppressWarnings("unused")
			Integer bVal = (Integer) value;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	@Override
	public byte[] getByteArray(Object value) {
		ByteBuffer buf = ByteBuffer.allocate(NUM_OF_BYTES);
		return buf.putInt((Integer)value).array();
	}

	@Override
	public Object getValue(ByteBuffer buf, int index, int length) {
		return getInt(buf, index);
	}
	
	@Override
	public int getInt(ByteBuffer buf, int index) {
		if (index >= 0) {
			return buf.getInt(index);
		} else {
			throw new IllegalArgumentException("The negative index : " + index);
		}
	}
	
}
