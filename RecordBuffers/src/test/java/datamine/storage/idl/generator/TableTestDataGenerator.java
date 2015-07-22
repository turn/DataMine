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
package datamine.storage.idl.generator;

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
import datamine.storage.idl.generator.java.InterfaceGenerator;
import datamine.storage.idl.generator.java.JavaCodeGenerator;
import datamine.storage.idl.generator.java.JavaTypeConvertor;
import datamine.storage.idl.generator.metadata.MetadataFileGenerator;
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.idl.type.PrimitiveFieldType;

/**
 * The class {@link TableTestDataGenerator} generates a set of classes storing the
 * test data.  
 * 
 * @author yqi
 */
public class TableTestDataGenerator implements ElementVisitor,
	UnaryOperatorInterface<Schema, Void> {

	private Map<Table, CodeGenerator> templateMap = null;
	private CodeTemplate currentTemplate = null;
	private Table currentTable = null;

	private final String sourceDir;
	private final String nameSpace;
	private final String interfaceNameSpace;
	private final String modelNameSpace;

	public TableTestDataGenerator(String sourcePath, String nameSpace,
			String modelNameSpace, String interfaceNameSpace) {
		this.sourceDir = sourcePath;
		this.nameSpace = nameSpace;
		this.interfaceNameSpace = interfaceNameSpace;
		this.modelNameSpace = modelNameSpace;
	}

	@Override
	public void visit(Schema schema) {
		templateMap = new HashMap<Table, CodeGenerator>();
	}


	public static String getTestDataClassName(Table table) {
		return getTestDataClassName(table.getName());
	}

	public static String getTestDataClassName(String tableName) {
		return new StringBuilder().append(CaseFormat.LOWER_UNDERSCORE.to(
				CaseFormat.UPPER_CAMEL, tableName)).append("TestData").toString();
	}

	public static String getMetaEnumName(Table table) {
		return getMetaEnumName(table.getName());
	}

	public static String getMetaEnumName(String tableName) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName) + "Metadata";
	}

	public static String getInterfaceGetterName(Field field) {
		return InterfaceGenerator.getGetterName(field);
	}

	public static String getInterfaceSetterName(Field field) {
		return InterfaceGenerator.getSetterName(field);
	}

	@Override
	public void visit(Table table) {
		final String[] classCode = {
				"public class {testDataClassName} extends AbstractTestData<{interfaceName}, {metaEnumName}> {",
				"",
				"    public {testDataClassName}(List<EnumMap<{metaEnumName}, Object>> input) {",
				"        super(input);",
				"    }",
				"",
				"    @Override",
				"    public List<{interfaceName}> createObjects(RecordBuilderInterface builder) {",
				"		List<{interfaceName}> records = Lists.newArrayList();",
				"		for (EnumMap<{metaEnumName}, Object> cur : data) {",
				"			{interfaceName} record = builder.build({interfaceName}.class);",
				"			{recordFieldSetters}",
				"			records.add(record);",
				"		}",
				"		return records;",
				"    }",
				"",
				"    @Override",
				"    public void assertObjects(List<{interfaceName}> objectList) {",
				"		int size = objectList.size();",
				"		Assert.assertEquals(size, data.size());",
				"		for (int i = 0; i < size; ++i) {",
				"			{assertFieldValues}",
				"		}",
				"	}",
				"",
				"    public static List<EnumMap<{metaEnumName}, Object>> createInputData(int num) {",
				"		List<EnumMap<{metaEnumName}, Object>> dataList = Lists.newArrayList();",
				"		for (int i = 0; i < num; ++i) {",
				"			EnumMap<{metaEnumName}, Object> dataMap = Maps.newEnumMap({metaEnumName}.class);",
				"			{setDataMap}",
				"			if (!dataMap.isEmpty()) {",
				"				dataList.add(dataMap);",
				"			}",
				"		}",
				"		return dataList;",
				"	}",
				"",
				"}",
				""
		};

		final String[] importString = {
				"import datamine.storage.api.RecordBuilderInterface;",
				"import " + this.modelNameSpace + ".*;",
				"import " + this.interfaceNameSpace + ".*;",
				"import datamine.storage.idl.generator.AbstractTestData;",
				"import datamine.storage.idl.generator.RandomValueGenerator;",
				"import datamine.storage.idl.type.*;",
				"",
				"import java.nio.ByteBuffer;",
				"import java.util.*;",
				"",
				"import org.testng.Assert;",
				"",
				"import com.google.common.collect.Lists;",
				"import com.google.common.collect.Maps;",
				""
		};

		CodeTemplate bodyTemplate = new CodeTemplate(classCode);
		CodeTemplate importTemplate = new CodeTemplate(importString);
		String className = getTestDataClassName(table); 
		JavaCodeGenerator javaCode = new JavaCodeGenerator(sourceDir, nameSpace, 
				className, importTemplate, bodyTemplate);
		bodyTemplate.fillFields("interfaceName", InterfaceGenerator.getInterfaceName(table.getName()));
		bodyTemplate.fillFields("testDataClassName", className);
		bodyTemplate.fillFields("metaEnumName", getMetaEnumName(table));
		templateMap.put(table, javaCode);
		currentTemplate = bodyTemplate;
		currentTable = table;
	}

	@Override
	public void visit(Field field) {
		if (!field.isDerived()) {
			currentTemplate.fillFields("recordFieldSetters", new FieldSetterTemplateGenerator(currentTable).apply(field));
			currentTemplate.fillFields("setDataMap", new FieldValueAssignmentTemplateGenerator(currentTable).apply(field));
		}
		
		currentTemplate.fillFields("assertFieldValues", new FieldAssertTemplateGenerator(currentTable).apply(field));
	}

	@Override
	public Void apply(Schema input) {
		Preconditions.checkNotNull(input);
		input.accept(this);

		// create wrapper classes: one per table
		for (CodeGenerator entry : templateMap.values()) {
			entry.generate();
		}

		// dummy return ...
		return null;
	}


	/**
	 * Create the code template for the setter function of each field. 
	 *  
	 * @author yqi
	 * @date Dec 16, 2014
	 */
	static class FieldSetterTemplateGenerator implements UnaryOperatorInterface<Field, CodeTemplate>{

		private Field field = null;
		private CodeTemplate fieldSetterTemplate = null;

		private String metadataClassName;
		private String enumName;

		public FieldSetterTemplateGenerator(Table table) {
			this.metadataClassName = getMetaEnumName(table);
		}

		private void fromPrimitiveFieldType(PrimitiveFieldType type) {
			String javaTypeStr = new JavaTypeConvertor(true).apply(type);

			String returnClause = new StringBuilder().append("(").append(javaTypeStr)
					.append(") cur.get(").append(metadataClassName).append(".")
					.append(enumName).append(")").toString();
			fieldSetterTemplate.fillFields("getFieldValue", returnClause);
		}

		private void fromGroupFieldType(GroupFieldType type) {

			final String getValueCode = new StringBuilder().append("new ")
					.append(getTestDataClassName(type.getGroupName()))
					.append("((List) cur.get(")
					.append(metadataClassName).append(".")
					.append(enumName)
					.append(")).createObjects(builder).get(0)").toString();
					
			fieldSetterTemplate.fillFields("getFieldValue", getValueCode);
		}

		private void fromCollectionFieldType(CollectionFieldType type) {

			FieldType elementType = type.getElementType();

			switch (type.getCollectionType()) {
			case LIST:

				if (elementType instanceof PrimitiveFieldType) {

					String getValueCode = new StringBuffer().append("(List) cur.get(")
							.append(metadataClassName).append(".")
							.append(enumName)
							.append(")").toString();

					fieldSetterTemplate.fillFields("getFieldValue", getValueCode);

				} else if (elementType instanceof GroupFieldType) {

					String getValueCode = new StringBuffer().append("new ")
							.append(getTestDataClassName(((GroupFieldType) elementType).getGroupName()))
							.append("((List) cur.get(")
							.append(metadataClassName).append(".")
							.append(enumName)
							.append(")).createObjects(builder)").toString();

					fieldSetterTemplate.fillFields("getFieldValue", getValueCode);

				} else {
					throw new IllegalArgumentException(
							"Not support in the LIST for the type of "+elementType);
				}

				break;

			default:
				throw new IllegalArgumentException("Not support for the type of " + type.getCollectionType());
			}

		}

		@Override
		public CodeTemplate apply(Field input) {

			final String[] code = {
					"",
					"			if (cur.containsKey({metaEnumName}.{enumName})) {",
					"				record.{fieldSetterName}({getFieldValue});",
					"			}",
			};

			Preconditions.checkNotNull(input);
			field = input;
			enumName = MetadataFileGenerator.getEnumValue(field.getName());
			fieldSetterTemplate = new CodeTemplate(code);

			fieldSetterTemplate.fillFields("fieldSetterName", getInterfaceSetterName(input));
			fieldSetterTemplate.fillFields("metaEnumName", metadataClassName);
			fieldSetterTemplate.fillFields("enumName", enumName);

			FieldType type = input.getType();
			if (type instanceof PrimitiveFieldType) {
				fromPrimitiveFieldType((PrimitiveFieldType) type);
			} else if (type instanceof GroupFieldType){
				fromGroupFieldType((GroupFieldType) type);
			} else if (type instanceof CollectionFieldType) {
				fromCollectionFieldType((CollectionFieldType) type);
			} else {
				throw new IllegalArgumentException("Not support the type of " + type.getClass());
			}

			return fieldSetterTemplate;
		}

	}

	/**
	 * Create the code template for the getter function of each field. 
	 *  
	 * @author yqi
	 * @date Dec 16, 2014
	 */
	static class FieldValueAssignmentTemplateGenerator implements UnaryOperatorInterface<Field, CodeTemplate> {

		private Field field = null;
		private CodeTemplate fieldSetterTemplate = null;

		private String metadataClassName;

		public FieldValueAssignmentTemplateGenerator(Table table) {
			this.metadataClassName = getMetaEnumName(table);
		}

		private void fromPrimitiveFieldType(PrimitiveFieldType type) {
	
			String returnClause = new StringBuilder()
				.append("RandomValueGenerator.getValueOf(((PrimitiveFieldType)")
				.append(metadataClassName).append(".")
				.append(MetadataFileGenerator.getEnumValue(field.getName()))
				.append(".getField().getType()).getPrimitiveType())").toString();
			fieldSetterTemplate.fillFields("getFieldValue", returnClause);
			fieldSetterTemplate.fillFields("checkConditions", "val != null");
		}

		private void fromGroupFieldType(GroupFieldType type) {

			final String getValueCode = new StringBuilder()
					.append(getTestDataClassName(type.getGroupName()))
					.append(".createInputData(1)").toString();
					
			fieldSetterTemplate.fillFields("getFieldValue", getValueCode);
			fieldSetterTemplate.fillFields("checkConditions", "val != null && !((List) val).isEmpty()");
		}

		private void fromCollectionFieldType(CollectionFieldType type) {

			FieldType elementType = type.getElementType();

			switch (type.getCollectionType()) {
			case LIST:

				if (elementType instanceof PrimitiveFieldType) {

					String returnClause = new StringBuilder()
					.append("RandomValueGenerator.getValueArrayOf(((PrimitiveFieldType) ((CollectionFieldType)")
					.append(metadataClassName).append(".")
					.append(MetadataFileGenerator.getEnumValue(field.getName()))
					.append(".getField().getType()).getElementType()).getPrimitiveType(), num)").toString();
					
					fieldSetterTemplate.fillFields("getFieldValue", returnClause);
					

				} else if (elementType instanceof GroupFieldType) {

					String getValueCode = new StringBuffer()
							.append(getTestDataClassName(((GroupFieldType) elementType).getGroupName()))
							.append(".createInputData(")
							.append("3")
							.append(")")
							.toString();

					fieldSetterTemplate.fillFields("getFieldValue", getValueCode);

				} else {
					throw new IllegalArgumentException(
							"Not support in the LIST for the type of "+elementType);
				}

				fieldSetterTemplate.fillFields("checkConditions", "val != null && !((List) val).isEmpty()");
				break;

			default:
				throw new IllegalArgumentException("Not support for the type of " + type.getCollectionType());
			}

		}

		@Override
		public CodeTemplate apply(Field input) {

			final String[] code = {
					"",
					"			{",
					"				Object val = {getFieldValue};",
					"				if ({checkConditions}) {",
					"					dataMap.put({metaEnumName}.{enumName}, val);",
					"				}",
					"			}",
			};

			Preconditions.checkNotNull(input);
			field = input;
			fieldSetterTemplate = new CodeTemplate(code);

			fieldSetterTemplate.fillFields("metaEnumName", metadataClassName);
			fieldSetterTemplate.fillFields("enumName", MetadataFileGenerator.getEnumValue(field.getName()));
			
			FieldType type = input.getType();
			if (type instanceof PrimitiveFieldType) {
				fromPrimitiveFieldType((PrimitiveFieldType) type);
			} else if (type instanceof GroupFieldType){
				fromGroupFieldType((GroupFieldType) type);
			} else if (type instanceof CollectionFieldType) {
				fromCollectionFieldType((CollectionFieldType) type);
			} else {
				throw new IllegalArgumentException("Not support the type of " + type.getClass());
			}

			return fieldSetterTemplate;
		}

	}

	/**
	 * Create the code template for the getter function of each field. 
	 *  
	 * @author yqi
	 * @date Dec 16, 2014
	 */
	static class FieldAssertTemplateGenerator implements UnaryOperatorInterface<Field, CodeTemplate> {

		private Field field = null;
		private String metadataClassName; 

		public FieldAssertTemplateGenerator(Table table) {
			this.metadataClassName = getMetaEnumName(table);
		}

		@Override
		public CodeTemplate apply(Field input) {

			Preconditions.checkNotNull(input);
			field = input;

			final String[] codeTemplate = {
					"",
					"			if (data.get(i).containsKey({metaEnumName}.{enumName})) {",
					"				{assertClause}",
					"			}",
			};
			String enumName = MetadataFileGenerator.getEnumValue(field.getName());
			CodeTemplate ct = new CodeTemplate(codeTemplate);
			ct.fillFields("metaEnumName", metadataClassName);
			ct.fillFields("enumName", enumName);
			
			FieldType type = input.getType();
			if (type instanceof PrimitiveFieldType) {

				String txt = new StringBuilder()
						.append("Assert.assertEquals(objectList.get(i).")
						.append(getInterfaceGetterName(input)).append("(), data.get(i).get(")
						.append(metadataClassName).append(".")
						.append(enumName)
						.append("));").toString();

				ct.fillFields("assertClause", txt);

			} else if (type instanceof GroupFieldType){
				
				String groupTestdataClassName = getTestDataClassName(((GroupFieldType) type).getGroupName());
				
				String txt = new StringBuilder()
						.append("new ").append(groupTestdataClassName)
						.append("((List) data.get(i).get(")
						.append(metadataClassName).append(".")
						.append(enumName)
						.append(")).assertObjects(Lists.newArrayList(objectList.get(i).")
						.append(getInterfaceGetterName(field))
						.append("()));").toString();
				ct.fillFields("assertClause", txt);
				
			} else if (type instanceof CollectionFieldType) {

				FieldType elementType = ((CollectionFieldType) type).getElementType();
				switch (((CollectionFieldType) type).getCollectionType()) {
				case LIST:
					if (elementType instanceof PrimitiveFieldType) {

						String txt = new StringBuilder()
								.append("Assert.assertEquals((List) objectList.get(i).")
								.append(getInterfaceGetterName(input)).append("(), data.get(i).get(")
								.append(metadataClassName).append(".")
								.append(enumName)
								.append("));").toString();
						ct.fillFields("assertClause", txt);

					} else if (elementType instanceof GroupFieldType) {

						String groupTestdataClassName = getTestDataClassName(
								((GroupFieldType) elementType).getGroupName());
						
						String txt = new StringBuilder()
								.append("new ").append(groupTestdataClassName)
								.append("((List) data.get(i).get(")
								.append(metadataClassName).append(".")
								.append(enumName)
								.append(")).assertObjects(objectList.get(i).")
								.append(getInterfaceGetterName(field))
								.append("());").toString();
						ct.fillFields("assertClause", txt);
						
					} else {
						throw new IllegalArgumentException(
								"Not support in the LIST for the type of " + elementType);
					}
					break;
				default:
					throw new IllegalArgumentException("Not support for the type of " + 
							((CollectionFieldType) type).getCollectionType());
				}
				
			} else {
				throw new IllegalArgumentException("Not support the type of " + type.getClass());
			}
			// exit
			return ct;
		}
	}
}
