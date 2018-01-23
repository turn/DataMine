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

import java.util.HashMap;
import java.util.Map;

/**
 * An enum for primitive type.
 *
 * <p>
 *     Note that the ID should be always larger than 0 and smaller than 1000
 *     (i.e.,{@link CollectionFieldType#MULTIPLICATION_FACTOR}).
 * </p>
 */
public enum PrimitiveType {

	BYTE (6, "BYTE"), 
	BOOL (7, "BOOL"),
	INT16 (8, "INT16"),
	INT32 (1, "INT32"),
	FLOAT (2, "FLOAT"),
	DOUBLE (3, "DOUBLE"),
	INT64 (4, "INT64"),
	STRING (5, "STRING"), 
	BINARY (9, "BINARY"),
	NULL (0, "NULL"),
	UNKNOWN (-1, "UNKNOWN"); 

	private int id;
	private String name;
	
	private static Map<Integer, PrimitiveType> idTypeMapping = 
			new HashMap<>();

	PrimitiveType(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public int getId() {
		return this.id;
	}
	
	public static PrimitiveType getType(int id) {
		if (idTypeMapping.isEmpty()) {
			for (PrimitiveType type : PrimitiveType.values()) {
				idTypeMapping.put(type.getId(), type);
			}
		}
		return idTypeMapping.get(id);
	}
}
