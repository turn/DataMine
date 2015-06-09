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
package datamine.storage.recordbuffers.idl.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

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
import datamine.storage.idl.type.PrimitiveType;

/**
 * The class {@link RecordMetaWrapperGenerator} generates the wrapper class 
 * for each table defined in the input schema. 
 * 
 * @author yqi
 * @date Dec 15, 2014
 */
public class RecordMetaWrapperGenerator implements ElementVisitor, 
	UnaryOperatorInterface<Schema, Void> {

	private Map<Table, CodeGenerator> templateMap = null;
	private CodeTemplate currentTemplate = null;
	private CodeTemplate derivedDefaultValueTemplate = null;
	private Table currentTable = null;

	private final String sourceDir;
	private final String nameSpace;
	private final String modelNameSpace;
	private final String interfaceNameSpace;


	public RecordMetaWrapperGenerator(String sourcePath, String nameSpace, 
			String modelNameSpace, String interfaceNameSpace) {
		this.sourceDir = sourcePath;
		this.nameSpace = nameSpace;
		this.modelNameSpace = modelNameSpace;
		this.interfaceNameSpace = interfaceNameSpace;
	}

	@Override
	public void visit(Schema schema) {
		templateMap = new HashMap<Table, CodeGenerator>();
	}

	public static String getBuilderClassName() {
		return "RecordBuffersBuilder";
	}

	
	public static String getClassName(Table table) {
		return getWrapperClassName(table.getName());
	}

	public static String getWrapperClassName(String tableName) {
		return new StringBuilder().append(CaseFormat.LOWER_UNDERSCORE.to(
				CaseFormat.UPPER_CAMEL, tableName)).append("Record").toString();
	}
	
	public static String getBaseClassName(Table table) {
		return getBaseClassName(table.getName());
	}

	public static String getBaseClassName(String tableName) {
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName) + "Metadata";
	}
	
	public static String getBaseClassGetterName(Field field) {
		return new StringBuilder().append("get").append(CaseFormat.LOWER_UNDERSCORE.to(
				CaseFormat.UPPER_CAMEL, field.getName())).toString();
	}

	public static String getBaseClassSetterName(Field field) {
		return new StringBuilder().append("set").append(CaseFormat.LOWER_UNDERSCORE.to(
				CaseFormat.UPPER_CAMEL, field.getName())).toString();
	}

	@Override
	public void visit(Table table) {
		final String[] classCode = {
				"public class {wrapperName} implements {interface} {",
				"    static final Logger LOG = LoggerFactory.getLogger({wrapperName}.class);",
				"",
				"    Record<{baseClassName}> value = null;",
				"	{variable4derivedColumns}",
				"",
				"    public {wrapperName}() {",
				"        value = new WritableRecord<{baseClassName}>({baseClassName}.class);",
				"    }",
				"",
				"    public {wrapperName}(Record<{baseClassName}> value) {",
				"        this.value = value;",
				"    }",
				"",
				"    @Override",
				"    public Object getBaseObject() {",
				"        return value;",
				"    }",
				"",
				"    @Override",
				"    public Class getBaseClass() {",
				"        return Record.class;",
				"    }",
				"",
				"    @Override",
				"    public void setBaseObject(Object obj) {",
				"        if (obj instanceof Record){",
				"            this.value = (Record<{baseClassName}>) obj;",
				"        }else{",
				"            throw new IllegalArgumentException(\"Not Support type of \"+obj.getClass());",
				"        }",
				"    }",
				"",
				"    @Override",
				"    public void referTo(BaseInterface right) {",
				"        this.value = (Record<{baseClassName}>) right.getBaseObject();",
				"    }",
				"",
				"    @Override",
				"    public void copyFrom(BaseInterface right) {",
				"		// note that it must be deep copy!!",
				"		this.value = new WritableRecord<{baseClassName}>({baseClassName}.class, ",
				"			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));",
				"    }",
				"",
				"    @Override",
				"    public boolean equals(Object that) {",
				"        if (that == null){",
				"            return false;",
				"        }",
				"        if (that instanceof {wrapperName}){",
				"            return this.getBaseObject().equals((({wrapperName}) that).getBaseObject());",
				"        }",
				"        return false;",
				"    }",
				"",
				"{fieldGetter}",
				"{fieldSetter}",
				"{fieldDefaultValue}",
				"{fieldListSize}",
				"{fieldComparable}",
				"{defaultDerivedClass}",
				"}"
		};

		final String[] importString = {
				"import " + this.modelNameSpace + ".*;",
				"import " + this.interfaceNameSpace + ".*;",
				"import datamine.storage.api.BaseInterface;",
				"import datamine.storage.recordbuffers.*;",
				"",
				"import java.nio.ByteBuffer;",
				"import java.util.*;",
				"",
				"import org.slf4j.Logger;",
				"import org.slf4j.LoggerFactory;",
				"import com.google.common.collect.Lists;",
				"import com.google.common.base.Strings;",
				""
		};
		
		CodeTemplate bodyTemplate = new CodeTemplate(classCode);
		CodeTemplate importTemplate = new CodeTemplate(importString);
		String className = getClassName(table); 
		JavaCodeGenerator javaCode = new JavaCodeGenerator(sourceDir, nameSpace, 
				className, importTemplate, bodyTemplate);
		bodyTemplate.fillFields("interface", InterfaceGenerator.getInterfaceName(table.getName()));
		bodyTemplate.fillFields("wrapperName", className);
		bodyTemplate.fillFields("baseClassName", getBaseClassName(table));
		templateMap.put(table, javaCode);
		currentTemplate = bodyTemplate;
		currentTable = table;
	}

	@Override
	public void visit(Field field) {
		currentTemplate.fillFields("fieldDefaultValue", new FieldGetterDefaultTemplateGenerator().apply(field));
		currentTemplate.fillFields("fieldListSize", new ListFieldSizeTemplateGenerator(currentTable).apply(field));
		if (!field.isDerived()) {
			currentTemplate.fillFields("fieldSetter", new FieldSetterTemplateGenerator(currentTable).apply(field));
			currentTemplate.fillFields("fieldGetter", new FieldGetterTemplateGenerator(currentTable).apply(field));
			
		} else {
			// Default value getter
			// we will borrow the code from above
			CodeTemplate codeTemp = new FieldGetterDefaultTemplateGenerator().apply(field);
			// the difference is that the method is renamed as that for common getters
			codeTemp.fillFields("interfaceGetterDefaultValueName", 
					InterfaceGenerator.getGetterName(field));
			createDerivedTableTemplate().fillFields("fieldDefaultValue", codeTemp);
			
			// Common getter
			codeTemp = new FieldGetterTemplateGenerator(currentTable).apply(field);
			StringBuilder sb = new StringBuilder();
			sb.append("return derivedFieldValues.").
			append(InterfaceGenerator.getGetterName(field)).append("();");
			codeTemp.fillFields("returnClause", sb.toString());
			currentTemplate.fillFields("fieldGetter", codeTemp);	
			
		}
		
		// dealing with the comparable field
		if (field.isSortKey()) {
			String interfaceName = InterfaceGenerator.getInterfaceName(currentTable.getName());
			String fieldGetter = InterfaceGenerator.getGetterName(field);
			StringBuffer sb = new StringBuffer();
			sb.append("\t@Override\n")
				.append("\tpublic int compareTo(").append(interfaceName).append(" o) {\n")
				.append("\t\t").append("return ").append(field.isAscSortKey() ? "-":"")
				.append("(o.").append(fieldGetter).append("() - this.").append(fieldGetter).append("());\n")
				.append("\t}\n");
			currentTemplate.fillFields("fieldComparable", sb.toString());
		}
	}
	
	private CodeTemplate createDerivedTableTemplate() {
		
		if (derivedDefaultValueTemplate != null) {
			return derivedDefaultValueTemplate;
		}
		
		// declare the variable for the derived field values
		String[] declarationCode = {
				"",
				"	private {derivedInterface} derivedFieldValues = new {defaultDerivedClass}();",
				"",
				"	public {wrapperName}(Record<{baseClassName}> record, {derivedInterface} derived) {",
				"		value = record;",
				"		derivedFieldValues = derived;",
				"	}",
				"",
				"	public void setDerivedValueImplementation({derivedInterface} derived) {",
				"		derivedFieldValues = derived;",
				"	}",
				""
		};
		String tableName = currentTable.getName();
		CodeTemplate derivedFieldsTemplate = new CodeTemplate(declarationCode);
		derivedFieldsTemplate.fillFields("derivedInterface", InterfaceGenerator.getDerivedInterfaceName(tableName));
		derivedFieldsTemplate.fillFields("defaultDerivedClass", InterfaceGenerator.getDefaultDerivedClassName(tableName));
		derivedFieldsTemplate.fillFields("wrapperName", getWrapperClassName(tableName));
		derivedFieldsTemplate.fillFields("baseClassName", getBaseClassName(tableName));
		currentTemplate.fillFields("variable4derivedColumns", derivedFieldsTemplate);
		
		// define the class for the derived fields
		String[] innerClassCode = {
				"",
				"	public static class {class} implements {interface} {",
				"",
				"	{fieldGetter}",
				"	{fieldDefaultValue}",
				"	}"
		};
		
		derivedDefaultValueTemplate = new CodeTemplate(innerClassCode);
		derivedDefaultValueTemplate.fillFields("interface", 
				InterfaceGenerator.getDerivedInterfaceName(tableName));
		derivedDefaultValueTemplate.fillFields("class", 
				InterfaceGenerator.getDefaultDerivedClassName(tableName));
		currentTemplate.fillFields("defaultDerivedClass", derivedDefaultValueTemplate);
		return derivedDefaultValueTemplate;		
	}
	
	private void generateRecordBuilder() {
		final String[] importString = {
				"",
				"import datamine.storage.api.BaseInterface;",
				"import datamine.storage.api.RecordBuilderInterface;",
				"import " + this.nameSpace + ".*;",
				"import " + this.interfaceNameSpace + ".*;",
				"",
				"import org.slf4j.Logger;",
				"import org.slf4j.LoggerFactory;",
				""
		};
		
		final String[] builderClassCode = {
				"public class {builderName} implements RecordBuilderInterface {",
				"",
				"	static final Logger LOG = LoggerFactory.getLogger({builderName}.class);",
				"",
				"	@SuppressWarnings(\"unchecked\")",
				"	@Override",
				"	public <T extends BaseInterface> T build(Class<T> tableClass) {",
				"",
				"		try {",
				"			{buildTableRecord}",
				"		} catch (InstantiationException e) {",
				"			LOG.error(\"The object can not be created for \" + tableClass + \":\" + e);",
				"		} catch (IllegalAccessException e) {",
				"			LOG.error(\"The object can not be created for \" + tableClass + \":\" + e);",
				"		}",
				"",
				"		LOG.error(\"Cannot create an instance for \"+tableClass);",
				"		throw new IllegalArgumentException(\"Not support for the record of \"+tableClass);",
				"	}",
				"}",
				""
		};
		
		final List<String> buildRecordCode = Lists.newArrayList(
				"		%s",
				"		if (tableClass == %s.class) {",
				"			return (T) %s.class.newInstance();",
				"		}"
		); 
				
		CodeTemplate bodyTemplate = new CodeTemplate(builderClassCode);
		CodeTemplate importTemplate = new CodeTemplate(importString);
		String className = getBuilderClassName(); 
		
		List<String> records = new ArrayList<String>();
		boolean isFirst = true;
		for (Table cur : templateMap.keySet()) {
			if (!isFirst) {
				records.add(String.format(buildRecordCode.get(0), "else"));
			} else {
				records.add("");
			}
			
			records.add(String.format(buildRecordCode.get(1), InterfaceGenerator.getInterfaceName(cur.getName())));
			records.add(String.format(buildRecordCode.get(2), getClassName(cur)));
			records.add(buildRecordCode.get(3));
			
			isFirst = false;
		}
		bodyTemplate.fillFields("builderName", className);
		bodyTemplate.fillFields("buildTableRecord", new CodeTemplate(records));

		new JavaCodeGenerator(sourceDir, nameSpace + ".builder", 
				className, importTemplate, bodyTemplate).generate();
	}
	
	@Override
	public Void apply(Schema input) {
		Preconditions.checkNotNull(input);
		input.accept(this);
		
		// create wrapper classes: one per table
		for (CodeGenerator entry : templateMap.values()) {
			entry.generate();
		}

		// generate the builder class
		generateRecordBuilder();
		
		// dummy return ...
		return null;
	}


	/**
	 * Create the code template for the getter function of each field. 
	 *  
	 * @author yqi
	 * @date Dec 16, 2014
	 */
	static class ListFieldSizeTemplateGenerator implements UnaryOperatorInterface<Field, CodeTemplate>{

		private Field field = null;
		private CodeTemplate fieldTemplate = null;
		private Table table;
		
		public ListFieldSizeTemplateGenerator(Table table) {
			this.table = table;
		}
		
		@Override
		public CodeTemplate apply(Field input) {

			final String[] code = {
					"	@Override",
					"	public int {interfaceGetterName}() {",
					"		return this.value.getListSize({metadataClassName}.{enumName});",
					"	}",
					""
			};
			
			Preconditions.checkNotNull(input);
			field = input;
			
			FieldType type = input.getType();
			if (type instanceof CollectionFieldType) {
				fieldTemplate = new CodeTemplate(code);
				fieldTemplate.fillFields("metadataClassName", 
						MetadataFileGenerator.getClassName(table));
				fieldTemplate.fillFields("enumName",
						MetadataFileGenerator.getEnumValue(field.getName()));
				fieldTemplate.fillFields("interfaceGetterName", 
						InterfaceGenerator.getListSizeGetterName(field));	
			} else {
				fieldTemplate = new CodeTemplate();
			}

			return fieldTemplate;
		}

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
		
		public FieldSetterTemplateGenerator(Table table) {
			this.metadataClassName = MetadataFileGenerator.getClassName(table);
		}

		private void fromPrimitiveFieldType(PrimitiveFieldType type) {
			
			fieldSetterTemplate.fillFields("defaultValueChecking", "1 == 1");
			
			// setValue clause
			String setValueStr = new StringBuffer().append("this.value.setValue(")
					.append(metadataClassName).append(".")
					.append(MetadataFileGenerator.getEnumValue(field.getName()))
					.append(", input);").toString();
			fieldSetterTemplate.fillFields("setValue", setValueStr);
		}

		private void fromGroupFieldType(GroupFieldType type) {

			fieldSetterTemplate.fillFields("defaultValueChecking", "input != null");

			final String[] loopCode = {
					"",
					"		{elementWrapperClass} iRec = ({elementWrapperClass}) input;",
					"		Record<{elementMatedataClass}> rec = (Record<{elementMatedataClass}>) iRec.getBaseObject();",
					"		this.value.setValue({metadataClassName}.{enumName}, rec);",
			};
			
			CodeTemplate ct = new CodeTemplate(loopCode);
			fieldSetterTemplate.fillFields("loopForList", ct);
			ct.fillFields("elementWrapperClass", getWrapperClassName(type.getGroupName()));
			ct.fillFields("elementMatedataClass", MetadataFileGenerator.getClassName(type.getGroupName()));
			ct.fillFields("metadataClassName", metadataClassName);
			ct.fillFields("enumName", MetadataFileGenerator.getEnumValue(field.getName()));
		}

		private void fromCollectionFieldType(CollectionFieldType type) {
			
			// defaultValueChecking
			fieldSetterTemplate.fillFields("defaultValueChecking", "input != null && !input.isEmpty()");
			
			// the rest
			FieldType elementType = type.getElementType();
			String elementTypeStr = new JavaTypeConvertor().apply(elementType);
			String typeStr = new JavaTypeConvertor().apply(type);
			
			switch (type.getType()) {
			case LIST:
				
				if (elementType instanceof PrimitiveFieldType) {
					
					final String[] loopCode = {
							"",
							"		{returnType} list = Lists.newArrayList();",
							"		for({elementType} elem : input){",
							"			list.add({newElement});",
							"		}",
							"		this.value.setValue({metadataClassName}.{enumName}, list);"
					};
					CodeTemplate ct = new CodeTemplate(loopCode);
					fieldSetterTemplate.fillFields("loopForList", ct);
					ct.fillFields("elementType", elementTypeStr);
					ct.fillFields("returnType", typeStr);
					ct.fillFields("newElement", "elem");
					ct.fillFields("metadataClassName", metadataClassName);
					ct.fillFields("enumName", MetadataFileGenerator.getEnumValue(field.getName()));
					
				} else if (elementType instanceof GroupFieldType) {
					
					final String[] loopCode = {
							"",
							"		List<Record> list = Lists.newArrayList();",
							"		for({elementType} elem : input){",
							"			{elementWrapperClass} iRec = ({elementWrapperClass}) elem;",
							"			Record<{elementMatedataClass}> rec = (Record<{elementMatedataClass}>) iRec.getBaseObject();",
							"			list.add(rec);",
							"		}",
							"		this.value.setValue({metadataClassName}.{enumName}, list);",
					};
					
					CodeTemplate ct = new CodeTemplate(loopCode);
					fieldSetterTemplate.fillFields("loopForList", ct);
					ct.fillFields("elementType", elementTypeStr);
					ct.fillFields("elementWrapperClass", getWrapperClassName(((GroupFieldType) elementType).getGroupName()));
					ct.fillFields("elementMatedataClass", MetadataFileGenerator.getClassName(((GroupFieldType) elementType).getGroupName()));
					ct.fillFields("metadataClassName", metadataClassName);
					ct.fillFields("enumName", MetadataFileGenerator.getEnumValue(field.getName()));
					
				} else {
					throw new IllegalArgumentException(
							"Not support in the LIST for the type of "+elementType);
				}
				
				break;

			default:
				throw new IllegalArgumentException("Not support for the type of " + type.getType());
			}
			
		}
		
		@Override
		public CodeTemplate apply(Field input) {

			final String[] code = {
					"",
					"	@Override",
					"	public void {interfaceSetterName}({returnType} input) {",
					"		if ({defaultValueChecking}) {",
					"			{loopForList}",
					"			{setValue}",
					"		}",
					"	}",
					""
			};
			
			Preconditions.checkNotNull(input);
			field = input;
			fieldSetterTemplate = new CodeTemplate(code);
			
			FieldType type = input.getType();
			String javaTypeStr = new JavaTypeConvertor().apply(type);
			fieldSetterTemplate.fillFields("returnType", javaTypeStr);
			fieldSetterTemplate.fillFields("interfaceSetterName", 
					InterfaceGenerator.getSetterName(field));
			
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
	static class FieldGetterDefaultTemplateGenerator implements UnaryOperatorInterface<Field, CodeTemplate> {

		private Field field = null;
		private CodeTemplate fieldGetterTemplate = null;

		@Override
		public CodeTemplate apply(Field input) {

			final String[] code = {
					"    @Override",
					"    public {returnType} {interfaceGetterDefaultValueName}() {",
					"        {defaultValueClause};",
					"    }",
					"",
			};
			
			Preconditions.checkNotNull(input);
			field = input;
			FieldType type = input.getType();
			
			if (type instanceof PrimitiveFieldType && ((PrimitiveFieldType) type).getType() != PrimitiveType.BINARY) {
				fieldGetterTemplate = new CodeTemplate(code);
				
				String javaTypeStr = new JavaTypeConvertor().apply(type);
				fieldGetterTemplate.fillFields("returnType", javaTypeStr);
				fieldGetterTemplate.fillFields("interfaceGetterDefaultValueName", 
						InterfaceGenerator.getDefaultValueGetterName(field));
				
				if (!field.isRequired()) {
					Object defVal = field.getDefaultValue();
					String valueStr = defVal.toString();
					if (type instanceof PrimitiveFieldType && 
						((PrimitiveFieldType) type).getType() == PrimitiveType.STRING) {
						valueStr = "\"" + valueStr + "\"";
						
					}
					String defaultValue = new StringBuilder().append("return (").append(javaTypeStr)
							.append(")").append(valueStr).toString();
					fieldGetterTemplate.fillFields("defaultValueClause", defaultValue);
				} else {
					String exceptionStr = new StringBuilder().append("throw new " +
							"NullPointerException(\"Require a valid value for ")
							.append(field.getName())
							.append("! Make sure the column has been selected!\")").toString();
					fieldGetterTemplate.fillFields("defaultValueClause", exceptionStr);
				}
	
			} else {
				fieldGetterTemplate = new CodeTemplate();
			}
			
			return fieldGetterTemplate;
		}

	}
	
	/**
	 * Create the code template for the getter function of each field. 
	 *  
	 * @author yqi
	 * @date Dec 16, 2014
	 */
	static class FieldGetterTemplateGenerator implements UnaryOperatorInterface<Field, CodeTemplate> {

		private Field field = null;
		private CodeTemplate fieldGetterTemplate = null;

		private String metadataClassName; 
		
		public FieldGetterTemplateGenerator(Table table) {
			this.metadataClassName = MetadataFileGenerator.getClassName(table);
		}
		
		private void fromPrimitiveFieldType(PrimitiveFieldType type) {

//			String javaTypeStr = new JavaTypeConvertor(true).apply(type);
			String enumName = MetadataFileGenerator.getEnumValue(field.getName());
			String colName = metadataClassName + "." + enumName;
			StringBuilder sb = new StringBuilder().append("return this.value.get");
			switch (type.getType()) {
			case BOOL:
				sb.append("Bool");
				break;
			case BYTE:
				sb.append("Byte");
				break;
			case INT16:
				sb.append("Short");
				break;
			case INT32:
				sb.append("Int");
				break;
			case INT64:
				sb.append("Long");
				break;
			case FLOAT:
				sb.append("Float");
				break;
			case DOUBLE:
				sb.append("Double");
				break;
			case STRING:
				sb.append("String");
				break;
			case BINARY:
				sb.append("Binary");
				break;
			default:
				throw new IllegalArgumentException("Not supported type : " + type.getType());
			}
			fieldGetterTemplate.fillFields("returnClause", 
					sb.append("(").append(colName).append(");").toString());
		}

		private void fromGroupFieldType(GroupFieldType type) {
			
			// last return clause
			String enumName = MetadataFileGenerator.getEnumValue(field.getName());
			String groupMetadataClassName = 
					MetadataFileGenerator.getClassName(type.getGroupName());
			
			final String[] retCode = {
					"",
					"		Record record = (Record) this.value.getValue({metadataClassName}.{enumName});",
					"		if (record == null) {",
					"			return null;",
					"		} else {",
					"			return new {groupWrapperClassName}(record);",
					"		}",
			};
			
			CodeTemplate ct = new CodeTemplate(retCode);
			fieldGetterTemplate.fillFields("returnClause", ct);
			ct.fillFields("metadataClassName", metadataClassName);
			ct.fillFields("enumName", enumName);
			ct.fillFields("groupWrapperClassName", getWrapperClassName(type.getGroupName()));
			ct.fillFields("groupMetadataClassName", groupMetadataClassName);
			
		}

		private void fromCollectionFieldType(CollectionFieldType type) {
			
			FieldType elementType = type.getElementType();
			String javaTypeStr = new JavaTypeConvertor(true).apply(elementType); 
			
			switch (type.getType()) {
			case LIST:

				fieldGetterTemplate.fillFields("elementType", javaTypeStr);
				
				final String[] loopCode = {
						"    	List<{elementType}> dList = Lists.newArrayList();",
						"		List<Object> sList = (List<Object>) this.value.getValue({metadataClass}.{enumName});",
						"		if(sList != null) {",
						"		   	for (Object cur : sList) {",
						"    			dList.add({newElementValue});",
						"	    	}",
						"		}",
						"       return dList;",
				};
				CodeTemplate ct = new CodeTemplate(loopCode);
				fieldGetterTemplate.fillFields("loopForList", ct);

				ct.fillFields("metadataClass", metadataClassName);
				ct.fillFields("enumName", MetadataFileGenerator.getEnumValue(field.getName()));
				
				if (elementType instanceof PrimitiveFieldType) {
					
					ct.fillFields("elementType", javaTypeStr);
					ct.fillFields("newElementValue", "(" + javaTypeStr+") cur");
					
				} else if (elementType instanceof GroupFieldType) {
					
					ct.fillFields("elementType", InterfaceGenerator.getInterfaceName(((GroupFieldType) elementType).getGroupName()));
					String createNewStructStr = new StringBuilder().append("new ")
							.append(getWrapperClassName(((GroupFieldType) elementType).getGroupName()))
							.append("((Record)cur)")
							.toString();
					ct.fillFields("newElementValue", createNewStructStr);					
				} else {
					throw new IllegalArgumentException(
							"Not support in the LIST for the type of " + elementType);
				}
				
				break;

			default:
				throw new IllegalArgumentException("Not support for the type of " + type.getType());
			}
			
		}
		
		@Override
		public CodeTemplate apply(Field input) {

			final String[] code = {
					"    @Override",
					"    public {returnType} {interfaceGetterName}() {",
					"        {loopForList}",
					"        {returnClause}",
					"    }",
					""
			};
			
			Preconditions.checkNotNull(input);
			field = input;
			fieldGetterTemplate = new CodeTemplate(code);
			
			FieldType type = input.getType();
			String javaTypeStr = new JavaTypeConvertor().apply(type);
			fieldGetterTemplate.fillFields("returnType", javaTypeStr);
			fieldGetterTemplate.fillFields("interfaceGetterName", InterfaceGenerator.getGetterName(field));

			
			if (type instanceof PrimitiveFieldType) {
				fromPrimitiveFieldType((PrimitiveFieldType) type);
			} else if (type instanceof GroupFieldType){
				fromGroupFieldType((GroupFieldType) type);
			} else if (type instanceof CollectionFieldType) {
				fromCollectionFieldType((CollectionFieldType) type);
			} else {
				throw new IllegalArgumentException("Not support the type of " + type.getClass());
			}

			return fieldGetterTemplate;
		}
	}
}
