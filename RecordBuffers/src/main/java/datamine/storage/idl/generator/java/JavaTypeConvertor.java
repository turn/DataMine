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
package datamine.storage.idl.generator.java;

import com.google.common.base.CaseFormat;

import datamine.operator.UnaryOperatorInterface;
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.CollectionType;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.idl.type.PrimitiveFieldType;

/**
 * It converts the type of the DataMine schema into that of the Java type. 
 * 
 * @author yqi
 * @date Dec 12, 2014
 */
public class JavaTypeConvertor implements UnaryOperatorInterface<FieldType, String>{
	
	private boolean useBoxing = false;
	
	public JavaTypeConvertor() {};
	
	public JavaTypeConvertor(boolean useBoxing) {
		this.useBoxing = useBoxing;
	}
	
	private String convert(PrimitiveFieldType type) {
		String javaType = "";
		switch (type.getPrimitiveType()) {
		case BOOL:
			javaType = useBoxing ? "Boolean" : "boolean";
			break;
		case BYTE:
			javaType = useBoxing ? "Byte" : "byte";
			break;
		case INT16:
			javaType = useBoxing ? "Short" : "short";
			break;
		case INT32:
			javaType = useBoxing ? "Integer" : "int";
			break;
		case INT64:
			javaType = useBoxing ? "Long" : "long";
			break;
		case FLOAT:
			javaType = useBoxing ? "Float" : "float";
			break;
		case DOUBLE:
			javaType = useBoxing ? "Double" : "double";
			break;
		case STRING:
			javaType = "String";
			break;
		case BINARY:
			javaType = "byte[]";
			break;
		default:
			throw new IllegalArgumentException("Not support the type: "+type.getPrimitiveType());
		}
		return javaType;
	}
	
	private String convert(GroupFieldType type) {
		String javaType = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, 
				type.getGroupName());
		
		// Note that the STRUCT is always accessed through its interface
		return new StringBuilder().append(javaType).append("Interface").toString();
	}
	
	private String convert(CollectionFieldType type) {
		StringBuilder sb = new StringBuilder();
		if (type.getCollectionType() == CollectionType.LIST) {
			useBoxing = true;
			sb.append("List<");
			sb.append(apply(type.getElementType()));
			sb.append(">");
			useBoxing = false;
		} else {
			throw new IllegalArgumentException("Not support "+type.getCollectionType());
		}
		return sb.toString();
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
