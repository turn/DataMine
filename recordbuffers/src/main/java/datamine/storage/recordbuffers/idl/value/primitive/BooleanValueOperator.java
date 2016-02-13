/**
 * Copyright (C) 2016 Turn Inc. (yan.qi@turn.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package datamine.storage.recordbuffers.idl.value.primitive;

import java.nio.ByteBuffer;

final public class BooleanValueOperator extends AbstractPrimitiveValueOperator {

	public BooleanValueOperator() {
		super(1);
	}

	@Override
	public boolean isValid(Object value) {
		try {
			@SuppressWarnings("unused")
			Boolean bVal = (Boolean) value;
		} catch (ClassCastException e) {
			return false;
		}
		return true;
	}

	@Override
	public byte[] getByteArray(Object value) {
		byte val = (byte) (((Boolean) value).equals(Boolean.TRUE) ? 1 : 0);
		return new byte[] {val};
	}

	@Override
	public Object getValue(ByteBuffer buf, int index, int length) {
		return getBool(buf, index);
	}
	
	public boolean getBool(ByteBuffer buf, int index) {
		if (index >= 0) {
			byte val = buf.get(index);
			return val == 1 ? true : false;
		} else {
			throw new IllegalArgumentException("The negative index : " + index);
		}
	}
}
