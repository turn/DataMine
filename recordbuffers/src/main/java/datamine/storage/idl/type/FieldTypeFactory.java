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
package datamine.storage.idl.type;

import java.util.EnumMap;
import java.util.Map;

import com.google.common.collect.Maps;

import datamine.storage.api.RecordMetadataInterface;

/**
 * The class provides a factory to create the type instance given the input. 
 * 
 * @author yqi
 */
public class FieldTypeFactory {
	final static EnumMap<PrimitiveType, PrimitiveFieldType> primitiveTypeMap = Maps.newEnumMap(PrimitiveType.class);
	final static Map<Class<? extends Enum<?>>, GroupFieldType> groupTypeMap = Maps.newHashMap();
	final static Map<FieldType, CollectionFieldType> collectionTypeMap = Maps.newHashMap();
	
	private FieldTypeFactory() {}
	
	public static FieldType getPrimitiveType(PrimitiveType type) {
		if (primitiveTypeMap.containsKey(type)) {
			return primitiveTypeMap.get(type);
		} else {
			PrimitiveFieldType fieldType = new PrimitiveFieldType(type);
			primitiveTypeMap.put(type, fieldType);
			return fieldType;
		}
	}
	
	public static FieldType getGroupType(Class<? extends Enum<?>> type) {
		if (groupTypeMap.containsKey(type)) {
			return groupTypeMap.get(type);
		} else {
			RecordMetadataInterface tmp = (RecordMetadataInterface) type.getEnumConstants()[0];
			GroupFieldType fieldType = new GroupFieldType(tmp.getTableName(), type);
			groupTypeMap.put(type, fieldType);
			return fieldType;
		}
	}
	
	public static FieldType getListType(FieldType type) {
		if (collectionTypeMap.containsKey(type)) {
			return collectionTypeMap.get(type);
		} else {
			CollectionFieldType fieldType = new CollectionFieldType(type, CollectionType.LIST);
			collectionTypeMap.put(type, fieldType);
			return fieldType;
		}
	}
}
