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

/**
 * The helper class defines dummy getters for the primitive value
 * @author yqi
 */
abstract public class FieldValueOperatorHelper implements FieldValueOperatorInterface {
	
	@Override
	public boolean getBool(ByteBuffer buffer, int index) {
		throw new IllegalAccessError("Not supported!");
	}

	@Override
	public byte getByte(ByteBuffer buffer, int index) {
		throw new IllegalAccessError("Not supported!");
	}

	@Override
	public short getShort(ByteBuffer buffer, int index) {
		throw new IllegalAccessError("Not supported!");
	}

	@Override
	public long getLong(ByteBuffer buffer, int index) {
		throw new IllegalAccessError("Not supported!");
	}

	@Override
	public int getInt(ByteBuffer buffer, int index) {
		throw new IllegalAccessError("Not supported!");
	}

	@Override
	public float getFloat(ByteBuffer buffer, int index) {
		throw new IllegalAccessError("Not supported!");
	}

	@Override
	public double getDouble(ByteBuffer buffer, int index) {
		throw new IllegalAccessError("Not supported!");
	}

	@Override
	public byte[] getBinary(ByteBuffer buffer, int index, int length) {
		throw new IllegalAccessError("Not supported!");
	}

	@Override
	public String getString(ByteBuffer buffer, int index, int length) {
		throw new IllegalAccessError("Not supported!");
	}
}
