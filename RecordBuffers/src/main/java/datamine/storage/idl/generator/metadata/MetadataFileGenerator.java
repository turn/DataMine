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

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;

import datamine.operator.UnaryOperatorInterface;
import datamine.storage.idl.ElementVisitor;
import datamine.storage.idl.Field;
import datamine.storage.idl.Schema;
import datamine.storage.idl.Table;
import datamine.storage.idl.generator.CodeGenerator;
import datamine.storage.idl.generator.CodeTemplate;
import datamine.storage.idl.generator.java.JavaCodeGenerator;
import datamine.storage.idl.generator.java.JavaTypeConvertor;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.idl.type.PrimitiveType;

/**
 * The class {@link MetadataFileGenerator} defines an approach to generate
 * the Java Enums to describe the table schema. 
 * 
 * @author yqi
 * @date Jan 6, 2015
 */
public class MetadataFileGenerator implements ElementVisitor, 
	UnaryOperatorInterface<Schema, Void> {

	private Map<Table, CodeGenerator> templateMap = null;
	private CodeTemplate currentTemplate = null;

	private final String sourceDir;
	private final String nameSpace;

	public MetadataFileGenerator(String sourcePath, String nameSpace) {
		this.sourceDir = sourcePath;
		this.nameSpace = nameSpace;
	}

	public static String getClassName(Table table) {
		return getClassName(table.getName());
	}

	public static String getClassName(String tableName) {
		return new StringBuilder().append(CaseFormat.LOWER_UNDERSCORE.to(
				CaseFormat.UPPER_CAMEL, tableName)).append("Metadata").toString();
	}

	public static String getEnumValue(String fieldName) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_UNDERSCORE, fieldName);
	}
	
	@Override
	public void visit(Schema schema) {
		templateMap = new HashMap<Table, CodeGenerator>();
	}
		
	@Override
	public void visit(Table table) {
		final String[] classCode = {
				"public enum {metadataClassName} implements RecordMetadataInterface {",
				"",
				"{enumerateFields};",
				"",
				"	static final short version = {version};",
				"	static final String name = {table_name};",
				"	private Field field;",
				"",
				"	private {metadataClassName}(short id, String name, FieldType type, ",
				"		boolean isRequired, Object defaultValue, boolean isDesSorted, ",
				"		boolean isAscSorted, boolean isFrequentlyUsed, boolean isDerived) {",
				"		field = Field.newBuilder(id, name, type).", 
				"				withDefaultValue(defaultValue).", 
				"				isRequired(isRequired).", 
				"				isDesSorted(isDesSorted).", 
				"				isAscSorted(isAscSorted).", 
				"				isFrequentlyUsed(isFrequentlyUsed).", 
				"				isDerived(isDerived).", 
				"				build();",
				"	}",
				"",
				"	@Override",
				"	public Field getField() { ",
				"		return field; ",
				"	}",
				"",
				"	@Override",
				"	public short getVersion() { ",
				"		return version; ",
				"	}",				
				"",
				"	@Override",
				"	public String getTableName() { ",
				"		return name; ",
				"	}",
				"}",
				""
		};

		final String[] importString = {
				"import datamine.storage.api.RecordMetadataInterface;",
				"import datamine.storage.idl.Field;" ,
				"import datamine.storage.idl.type.FieldType;" ,
				"import datamine.storage.idl.type.PrimitiveType;",
				"import datamine.storage.idl.type.FieldTypeFactory;" ,
				"",
		};


		CodeTemplate bodyTemplate = new CodeTemplate(classCode);
		CodeTemplate importTemplate = new CodeTemplate(importString);
		String className = getClassName(table); 
		JavaCodeGenerator javaCode = new JavaCodeGenerator(sourceDir, nameSpace, 
				className, importTemplate, bodyTemplate);
		bodyTemplate.fillFields("metadataClassName", className);
		bodyTemplate.fillFields("comparatorImplementation", getComparatorImplementation(table));
		bodyTemplate.fillFields("version", table.getVersion() + "");
		bodyTemplate.fillFields("table_name", "\"" + table.getName() + "\"");
		templateMap.put(table, javaCode);
		currentTemplate = bodyTemplate;		
	}

	/**
	 * For the moment it is not used. 
	 * @param table
	 * @return
	 */
	private String getComparatorImplementation(Table table) {
		return "null;";
	}
	
	@Override
	public void visit(Field field) {

		StringBuilder sb = new StringBuilder();
		sb.append("\t");
		sb.append(getEnumValue(field.getName())).append("(");
		sb.append("(short)").append(field.getId()).append(", \"").append(field.getName()).append("\", ");
		sb.append(new MetaTypeConvertor().apply(field.getType())).append(", ");
		sb.append(field.isRequired() ? "true, " : "false, ");
		
		FieldType type = field.getType();
		// the field does not have the default unless it is optional and primitive
		if (field.isRequired() || !(type instanceof PrimitiveFieldType) ||
			(type instanceof PrimitiveFieldType && ((PrimitiveFieldType)type).getType() == PrimitiveType.BINARY)) {
			sb.append("null"); // default value
			
		} else {
			String javaTypeStr = new JavaTypeConvertor().apply(type);
			Object defVal = field.getDefaultValue();
			String valueStr = defVal.toString();
			if (type instanceof PrimitiveFieldType && 
			   ((PrimitiveFieldType) type).getType() == PrimitiveType.STRING) {
				valueStr = "\"" + valueStr + "\"";
				
			}
			sb.append("(").append(javaTypeStr).append(")").append(valueStr);	
		}	

		// constraints of the field
		sb.append(field.isDesSortKey() ? ", true" : ", false");
		sb.append(field.isAscSortKey() ? ", true" : ", false");
		sb.append(field.isFrequentlyUsed() ? ", true" : ", false");
		sb.append(field.isDerived()? ", true" : ", false");
		sb.append("),");		
		
		CodeTemplate code = new CodeTemplate(new String[]{sb.toString()});
		currentTemplate.fillFields("enumerateFields", code);
	}

	@Override
	public Void apply(Schema input) {
		Preconditions.checkNotNull(input);
		input.accept(this);
		
		// create wrapper classes: one per table
		for (CodeGenerator entry : templateMap.values()){
			entry.generate();
		}

		// dummy return ...
		return null;
	}

}
