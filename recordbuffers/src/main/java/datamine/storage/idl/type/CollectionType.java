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
 * The enum for the definition of collection.
 */
public enum CollectionType {
	LIST(1, "LIST"),
	SET(2, "SET");

	private int id;
	private String name;

	private static Map<Integer, CollectionType> idTypeMapping =
		new HashMap<>();

	CollectionType(int id, String name) {
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

	public static CollectionType getType(int id) {
		if (idTypeMapping.isEmpty()) {
			for (CollectionType type : values()) {
				idTypeMapping.put(type.getId(), type);
			}
		}
		return idTypeMapping.get(id);
	}
}