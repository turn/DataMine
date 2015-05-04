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

import datamine.storage.idl.FieldValueOperatorInterface;

abstract public class AbstractPrimitiveValueOperator implements FieldValueOperatorInterface {

	protected final int NUM_OF_BYTES;
	
	public AbstractPrimitiveValueOperator(int numOfBytes) {
		NUM_OF_BYTES = numOfBytes;
	}
	
	@Override
	public boolean hasFixedLength() {
		return true;
	}

	@Override
	public int getNumOfBytes(Object val) {
		return NUM_OF_BYTES;
	}

	@Override
	public int getMetadataSize() {
		return 0;
	}
}