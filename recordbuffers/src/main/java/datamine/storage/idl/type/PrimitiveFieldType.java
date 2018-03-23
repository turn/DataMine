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
package datamine.storage.idl.type;


/**
 * It is conceptually a wrapper class over the ENUM for primitive attribute
 * 
 * @author yqi
 * @date Jan 8, 2015
 */
final public class PrimitiveFieldType implements FieldType {

	final private PrimitiveType type;
	
	public PrimitiveFieldType(PrimitiveType gType) {
		this.type = gType;
	}

	public PrimitiveType getPrimitiveType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		PrimitiveFieldType other = (PrimitiveFieldType) obj;
		if (type != other.type)
			return false;
		return true;
	}


	@Override
	public int getID() {
		return type.getId();
	}

	@Override
	public String toString() {
		return type.getName();
	}
}
