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
package datamine.storage.idl.json;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import datamine.operator.UnaryOperatorInterface;
import datamine.storage.idl.Field;
import datamine.storage.idl.Schema;
import datamine.storage.idl.Table;
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.CollectionType;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.idl.type.PrimitiveType;

/**
 * The class creates a schema from its JSON counterpart.  
 * 
 * @author yqi
 * @date Dec 10, 2014
 */
public class JsonSchemaConvertor implements JsonElementVisitor, 
	UnaryOperatorInterface<String, Schema> {

	static final Logger logger = LoggerFactory.getLogger(JsonSchemaConvertor.class);
	
	private String name;
	private List<Table> tables = null;
	private List<Field> fields = null;
	private Set<String> embededTableName = new HashSet<String>();
	private Set<String> tableName = new HashSet<String>();
	
	@Override
	public void visit(JsonSchema schema) {
		name = schema.getName();
		tables = new ArrayList<Table>();
	}
 
	@Override
	public void visit(JsonTable table) {
		String name = table.getName();
		tableName.add(name);
		fields = new ArrayList<Field>();
		tables.add(new Table(name, fields, 
				Short.parseShort(table.getVersion())));
	}

	@Override
	public void visit(JsonField field) {
		String fName = field.getName();
		int fId = field.getId();
		String typeStr = field.getType();
		FieldType type = getType(typeStr);
		Object defaultValue = getDefaultValue(
				JsonFieldType.getType(typeStr), field.getDefaultValueString());
		
		// Sanity check: 
		/// 1. an optional primitive field must have a default value
		if (defaultValue == null && type instanceof PrimitiveFieldType && !field.isRequired()) {
			if (((PrimitiveFieldType) type).getType() != PrimitiveType.BINARY) {
				throw new IllegalArgumentException("Default value " +
						"is required for the optional primitive column - " + field.getName());				
			}
		}
		
		/// 2. sort-key column must be required
		if (field.isSorted() && !field.isRequired()) {
			throw new IllegalArgumentException(
					"The sort-key column must be required! - " + field.getName());
		}
		
		fields.add(Field.newBuilder(fId, fName, type).
				withDefaultValue(defaultValue).
				isRequired(field.isRequired()).
				isDesSorted(field.isDesSorted()).
				isAscSorted(field.isAscSorted()).
				isFrequentlyUsed(field.isFrequentlyUsed()).
				isDerived(field.isDerived()).
				hasLargeList(field.hasLargeList()).
				build());
	}

	private Object getDefaultValue(JsonFieldType type, String defaultValueStr) {
		if (defaultValueStr.endsWith(JsonField.INIT_DEFAULT)) {
			return null;
		} else {
			return type.parseValue(defaultValueStr); 
		}
	}
	
	private FieldType getType(String typeStr) {
		switch (JsonFieldType.getType(typeStr)) {
		case Boolean:
			return new PrimitiveFieldType(PrimitiveType.BOOL);
		case Byte:
			return new PrimitiveFieldType(PrimitiveType.BYTE); 
		case Short:
			return new PrimitiveFieldType(PrimitiveType.INT16); 
		case Integer: 
			return new PrimitiveFieldType(PrimitiveType.INT32);
		case Long:
			return new PrimitiveFieldType(PrimitiveType.INT64);
		case Float:
			return new PrimitiveFieldType(PrimitiveType.FLOAT);
		case Double:
			return new PrimitiveFieldType(PrimitiveType.DOUBLE); 
		case String:
			return new PrimitiveFieldType(PrimitiveType.STRING);
		case Binary:
			return new PrimitiveFieldType(PrimitiveType.BINARY);
		case Struct:
			embededTableName.add(typeStr);
			return new GroupFieldType(typeStr, null);
		case List:
			String elementTypeName = JsonFieldType.getElementTypeNameInList(typeStr);
			return new CollectionFieldType(getType(elementTypeName), 
					CollectionType.LIST);
		}
		throw new IllegalArgumentException("Illegal input type: " + typeStr);
	}
	
	
	@Override
	public Schema apply(String jsonStr) {
		JsonSchema js = new Gson().fromJson(jsonStr, JsonSchema.class);
		if (js == null) {
			throw new IllegalArgumentException("No valid JsonSchema can be found in \n" + jsonStr);
		}
		js.accept(this);
		// validate the schema
		embededTableName.removeAll(tableName);
		if (!embededTableName.isEmpty()) {
			throw new IllegalArgumentException("Not a valid JsonSchema because " +
					"no definition can be found for " + embededTableName);
		}
		return new Schema(name, tables);
	}
}
