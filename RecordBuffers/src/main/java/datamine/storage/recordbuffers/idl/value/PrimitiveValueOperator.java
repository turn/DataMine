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
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.recordbuffers.idl.value.primitive.BinaryValueOperator;
import datamine.storage.recordbuffers.idl.value.primitive.BooleanValueOperator;
import datamine.storage.recordbuffers.idl.value.primitive.ByteValueOperator;
import datamine.storage.recordbuffers.idl.value.primitive.DoubleValueOperator;
import datamine.storage.recordbuffers.idl.value.primitive.FloatValueOperator;
import datamine.storage.recordbuffers.idl.value.primitive.Int16ValueOperator;
import datamine.storage.recordbuffers.idl.value.primitive.Int32ValueOperator;
import datamine.storage.recordbuffers.idl.value.primitive.Int64ValueOperator;
import datamine.storage.recordbuffers.idl.value.primitive.StringValueOperator;

/**
 * The operator is defined for the primitive value
 * 
 * @author yqi
 */
class PrimitiveValueOperator extends FieldValueOperatorHelper {

	private final FieldValueOperatorInterface valueOpr;
	private final PrimitiveFieldType primitiveType;
	
	public PrimitiveValueOperator(PrimitiveFieldType type) {
		this.primitiveType = type;
		switch (type.getType()) {
		case BYTE:
			valueOpr = new ByteValueOperator();
			break;
		case BOOL:
			valueOpr = new BooleanValueOperator();
			break;
		case INT16:
			valueOpr = new Int16ValueOperator();
			break;
		case INT32:
			valueOpr = new Int32ValueOperator();
			break;
		case INT64: 
			valueOpr = new Int64ValueOperator();
			break;
		case FLOAT:
			valueOpr = new FloatValueOperator();
			break;
		case DOUBLE:
			valueOpr = new DoubleValueOperator();
			break;
		case STRING:
			valueOpr = new StringValueOperator();
			break;
		case BINARY:
			valueOpr = new BinaryValueOperator();
			break;
		case UNKNOWN:
		default:
			valueOpr = null;
			throw new IllegalArgumentException("It is not a valid primitive type - " + type.getType());
		}
		
	}
	
	@Override
	public boolean isValid(Object val) {
		return valueOpr.isValid(val);
	}

	@Override
	public byte[] getByteArray(Object val) {
		return valueOpr.getByteArray(val);
	}

	@Override
	public Object getValue(ByteBuffer buffer, int index, int length) {
		return valueOpr.getValue(buffer, index, length);
	}

	@Override
	public boolean hasFixedLength() {
		return valueOpr.hasFixedLength();
	}

	@Override
	public int getNumOfBytes(Object val) {
		return valueOpr.getNumOfBytes(val);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((primitiveType == null) ? 0 : primitiveType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrimitiveValueOperator other = (PrimitiveValueOperator) obj;
		if (valueOpr == null) {
			if (other.valueOpr != null)
				return false;
		} else if (!valueOpr.equals(other.valueOpr))
			return false;
		return true;
	}

	@Override
	public int getMetadataLength() {
		return valueOpr.getMetadataLength();
	}

	

}
