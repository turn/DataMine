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
package datamine.storage.idl.generator.metadata;

import datamine.operator.UnaryOperatorInterface;
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.idl.type.PrimitiveFieldType;

/**
 * It converts the type of the DataMine schema into that of the metadata type. 
 * 
 * @author yqi
 * @date Jan 07, 2015
 */
public class MetaTypeConvertor implements UnaryOperatorInterface<FieldType, String>{
	
	private String convert(PrimitiveFieldType type) {
		String typeFormat = "FieldTypeFactory.getPrimitiveType(PrimitiveType.%s)";
		switch (type.getType()) {
		case BOOL:
			return String.format(typeFormat, "BOOL");
		case BYTE:
			return String.format(typeFormat, "BYTE");
		case INT16:
			return String.format(typeFormat, "INT16");
		case INT32:
			return String.format(typeFormat, "INT32");
		case INT64:
			return String.format(typeFormat, "INT64");
		case FLOAT:
			return String.format(typeFormat, "FLOAT");
		case DOUBLE:
			return String.format(typeFormat, "DOUBLE");
		case STRING:
			return String.format(typeFormat, "STRING");
		case BINARY:
			return String.format(typeFormat, "BINARY");
		default:
			throw new IllegalArgumentException("Not support the type: "+type.getType());
		}
	}
	
	private String convert(GroupFieldType type) {
		String typeFormat = "FieldTypeFactory.getGroupType(%s.class)";
		String className = MetadataFileGenerator.getClassName(type.getGroupName());
		return String.format(typeFormat, className);
	}
	
	private String convert(CollectionFieldType type) {
		String typeFormat = "FieldTypeFactory.getListType(%s)";
		FieldType elmType = type.getElementType();
		if (elmType instanceof PrimitiveFieldType) {
			return String.format(typeFormat, convert(((PrimitiveFieldType) elmType)));
		} else if (elmType instanceof GroupFieldType){
			return String.format(typeFormat, convert(((GroupFieldType) elmType)));
		} else {
			throw new IllegalArgumentException("Not support the embeded list type");	
		}
	}

	@Override
	public String apply(FieldType type) {
		if (type instanceof PrimitiveFieldType) {
			return convert((PrimitiveFieldType)type);
		} else if (type instanceof GroupFieldType) {
			return convert((GroupFieldType)type);
		} else if (type instanceof CollectionFieldType) {
			return convert((CollectionFieldType)type);
		}
		throw new IllegalArgumentException("Not support this type "+type);
	}
}
