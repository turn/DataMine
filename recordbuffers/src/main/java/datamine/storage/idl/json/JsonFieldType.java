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
package datamine.storage.idl.json;

import com.google.common.collect.Lists;

import org.apache.commons.lang3.ArrayUtils;

/**
 * The ENUM defines all types in the JSON schema. 
 * 
 * @author yqi
 * @date Dec 11, 2014
 */
public enum JsonFieldType {

	Boolean("Boolean", true) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			return java.lang.Boolean.parseBoolean(valueStr);
		}
	},
	Byte("Byte", true) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			return java.lang.Byte.parseByte(valueStr);
		}
	},
	Short("Short", true) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			return java.lang.Short.parseShort(valueStr);
		}
	},
	Integer("Integer", true) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			return java.lang.Integer.parseInt(valueStr);
		}
	},
	Long("Long", true) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			return java.lang.Long.parseLong(valueStr);
		}
	},
	Float("Float", true) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			return java.lang.Float.parseFloat(valueStr);
		}
	},
	Double("Double", true) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			return java.lang.Double.parseDouble(valueStr);
		}
	},
	String("String", true) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			return valueStr;
		}
	},
	Binary("Binary", true) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			// verify the input
			if (!valueStr.startsWith("[") || !valueStr.endsWith("]")) {
				throw new IllegalArgumentException("A value of binary should start "
						+ "with '[' and end with ']', e.g., [2, 3]");
			}
			// convert the input into a value
			String[] byteValues = valueStr.substring(1, valueStr.length() - 1).split(",");
			java.util.List<Byte> list = Lists.newArrayList();

			for (String cur : byteValues) {
				if (cur.trim().isEmpty()) {
					continue;
				}
				list.add(java.lang.Byte.parseByte(cur.trim()));
			}
			return ArrayUtils.toPrimitive(list.toArray(new Byte[list.size()]));
		}
	},
	Struct("Struct", false) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			throw new IllegalAccessError("Conversion from String to Struct is not supported!");
		}
	},
	List("List", false) {
		@Override
		Object parseValue(java.lang.String valueStr) {
			throw new IllegalAccessError("Conversion from String to List is not supported!");
		}
	};

	static final String JSON_LIST_ELEMENT_TYPE_DELIMITER = ":";
	String name;
	boolean isPrimitive;

	private JsonFieldType(String name, boolean isPrimitive) {
		this.name = name;
		this.isPrimitive = isPrimitive;
	}

	abstract Object parseValue(String valueStr);

	/**
	 * Get the corresponding type given a text input. 
	 * <p>
	 * Note that it is treated as a STRUCT if the input could not be found to
	 * be predefined. 
	 * </p>
	 * 
	 * @param name the input text for type
	 * @return the corresponding JSON type
	 */
	public static JsonFieldType getType(String name) {
		if (name.startsWith("List:")) {
			return JsonFieldType.List;
		} else {
			for (JsonFieldType cur : values()) {
				if (cur.name.endsWith(name)) {
					return cur;
				}
			}
			return JsonFieldType.Struct;
		}
	}

	/**
	 * Return the JSON field type of the element in a LIST structure.
	 * @param name the text representation of a JSON LIST type. 
	 * @return the JSON field type of the element in a LIST structure.
	 */
	public static JsonFieldType getElementTypeInList(String name) {
		return getType(getElementTypeNameInList(name));
	}

	/**
	 * Get the type name of the element in a LIST structure.
	 * @param name the text representation of a JSON LIST type.
	 * @return the type name of the element in a LIST structure.
	 */
	public static String getElementTypeNameInList(String name) {
		if (getType(name) != JsonFieldType.List) {
			throw new IllegalArgumentException("List:XXX is required!");
		} else {
			return name.substring(name.indexOf(JSON_LIST_ELEMENT_TYPE_DELIMITER) + 1);
		}
	}
}
