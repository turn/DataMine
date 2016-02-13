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

/**
 * The operation is defined for the float type. 
 * 
 * <p> 
 * Note that a valid float number should be smaller than 1E20. 
 * </p>
 *  
 * @author yqi
 */
final public class FloatValueOperator extends AbstractPrimitiveValueOperator {

	public FloatValueOperator() {
		super(4);
	}
	
	@Override
	public boolean isValid(Object value) {
		Float fVal = (Float) value;
		return !(fVal.isInfinite() || fVal.isNaN() || fVal > 1E20);
	}

	@Override
	public byte[] getByteArray(Object value) {
		ByteBuffer buf = ByteBuffer.allocate(4);
		return buf.putFloat((Float) value).array();
	}

	@Override
	public Object getValue(ByteBuffer buf, int index, int length) {
		return getFloat(buf, index);
	}
	
	public float getFloat(ByteBuffer buf, int index) {
		if (index >= 0) {
			return buf.getFloat(index);
		} else {
			throw new IllegalArgumentException("The negative index : " + index);
		}
	}
	
}
