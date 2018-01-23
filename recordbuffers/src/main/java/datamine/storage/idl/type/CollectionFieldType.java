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

final public class CollectionFieldType implements FieldType {

	public static final int MULTIPLICATION_FACTOR = 1000;

	private final CollectionType collectionType;
	private final FieldType elementType;

	public CollectionFieldType(FieldType elementType, CollectionType cType) {
		this.elementType = elementType;
		this.collectionType = cType;
	}

	public FieldType getElementType() {
		return elementType;
	}

	public CollectionType getCollectionType() {
		return collectionType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((collectionType == null) ? 0 : collectionType.hashCode());
		result = prime * result
				+ ((elementType == null) ? 0 : elementType.hashCode());
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
		CollectionFieldType other = (CollectionFieldType) obj;
		if (collectionType != other.collectionType)
			return false;
		if (elementType == null) {
			if (other.elementType != null)
				return false;
		} else if (!elementType.equals(other.elementType))
			return false;
		return true;
	}

	/**
	 * The type ID of {@link CollectionFieldType} is calculated by
	 * its own type ID and its element type ID, based on the formula below:
	 *
	 * <p>
	 *     collectionType.getId() * 1000 + elementType.getID()
	 * </p>
	 * @return
	 */
	@Override
	public int getID() {
		return collectionType.getId() * MULTIPLICATION_FACTOR + elementType.getID();
	}

	@Override
	public String toString() {
		return collectionType.getName() + "<" + elementType.toString() + ">";
	}
}
