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

import java.util.Map;

import com.google.common.base.CaseFormat;
import com.google.common.collect.Maps;

import datamine.storage.idl.ElementVisitor;
import datamine.storage.idl.Field;
import datamine.storage.idl.Schema;
import datamine.storage.idl.Table;
import datamine.storage.idl.generator.CodeGenerator;
import datamine.storage.idl.generator.CodeTemplate;
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.CollectionType;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.idl.type.PrimitiveType;

/**
 * The generator creates a Java Interface for each table of the input schema. 
 * 
 * @author yqi
 * @author tliu
 * @date Dec 15, 2014
 */
public class InterfaceGenerator implements CodeGenerator, ElementVisitor {
	
	private Map<Table, CodeGenerator> templateMap = null;
	private Map<Table, CodeGenerator> derivedTemplateMap = null;
	private CodeTemplate currentTemplate = null;
	private CodeTemplate currentDerivedTemplate = null;
	private Table currentTable = null;
	private boolean hasDerivedImplementationSet = false;
	
	private final String sourceDir;
	private final String nameSpace;
	
	public InterfaceGenerator(String sourceDir, String nameSpace) {
		this.sourceDir = sourceDir;
		this.nameSpace = nameSpace;
	}
	
	@Override
	public void visit(Schema schema) {
		templateMap = Maps.newHashMap();
		derivedTemplateMap = Maps.newHashMap();
	}

	private void createDerivedTableTemplate(Table table) {
		
		if (derivedTemplateMap.containsKey(table)) {
			return;
		}
		
		String[] classCode = {
				"public interface {interface} {",
				"",
				"{fieldDefaultValue}",
				"}"
		};
		
		CodeTemplate bodyTemplate = new CodeTemplate(classCode);
		CodeTemplate importTemplate = new CodeTemplate();
		String className = getDerivedInterfaceName(table.getName());
		JavaCodeGenerator javaCode = new JavaCodeGenerator(sourceDir,
				nameSpace, className, importTemplate, bodyTemplate);
		bodyTemplate.fillFields("interface", className);
		derivedTemplateMap.put(table, javaCode);
		currentDerivedTemplate = bodyTemplate;
	}
	
	@Override
	public void visit(Table table) {
		String[] classCode = {
				"public interface {interface} extends BaseInterface {ComparableInterface} {",
				"",
				"{fieldGetter}",
				"{fieldSetter}",
				"{fieldDefaultValue}",
				"{fieldListSize}",
				"{setDerivedImplementation}",
				"}"
		};
		
		String[] importString = {
				"import datamine.storage.api.BaseInterface;",
				"",
				"import java.util.List;"
		};
		
		CodeTemplate bodyTemplate = new CodeTemplate(classCode);
		CodeTemplate importTemplate = new CodeTemplate(importString);
		String className = getInterfaceName(table.getName());
		JavaCodeGenerator javaCode = new JavaCodeGenerator(sourceDir,
				nameSpace, className, importTemplate, bodyTemplate);
		bodyTemplate.fillFields("interface", getInterfaceName(table.getName()));
		templateMap.put(table, javaCode);
		currentTemplate = bodyTemplate;
		currentTable = table;
	}

	@Override
	public void visit(Field field) {
		InterfaceTemplate templateGenerator = new InterfaceTemplate(field);
		
		FieldType type = field.getType();
		
		// decide if a derived interface is necessary
		if (field.isDerived()) {
			createDerivedTableTemplate(currentTable);
			currentDerivedTemplate.fillFields("fieldDefaultValue", 
					templateGenerator.getGetterTemplate());
		}
		
		// generate get list size for list field
		if (type instanceof CollectionFieldType) {
			if (((CollectionFieldType) type).getCollectionType() == CollectionType.LIST) {
				currentTemplate.fillFields("fieldListSize", 
						templateGenerator.getListSizeTemplate());	
			} else {
				throw new IllegalArgumentException("Not support the type of " + 
						((CollectionFieldType) type).getCollectionType());
			}
		}
		
		//generate getDefault function only for primitive field
		if (type instanceof PrimitiveFieldType && ((PrimitiveFieldType) type).getPrimitiveType() != PrimitiveType.BINARY) {
			currentTemplate.fillFields("fieldDefaultValue", 
					templateGenerator.getDefaultValueTemplate());
		}
				
		//generate getter and setter for all the fields
		currentTemplate.fillFields("fieldGetter", 
				templateGenerator.getGetterTemplate());
		
		if (!field.isDerived()) {
			currentTemplate.fillFields("fieldSetter", 
					templateGenerator.getSetterTemplate());	
		} else if (!hasDerivedImplementationSet) {
			final String[] codes = {
				"		public void setDerivedValueImplementation({derivedInterface} derived);"
			};
			CodeTemplate ct = new CodeTemplate(codes);
			ct.fillFields("derivedInterface", getDerivedInterfaceName(currentTable.getName()));
			currentTemplate.fillFields("setDerivedImplementation", ct);
			hasDerivedImplementationSet = true;
		}
				
		if (field.isSortKey()) {
			StringBuilder sb = new StringBuilder().append(", ")
					.append("Comparable<")
					.append(getInterfaceName(currentTable.getName()))
					.append(">");
			currentTemplate.fillFields("ComparableInterface", sb.toString());
		}
	}

	@Override
	public void generate() {
		// generate code for the interface
		for (CodeGenerator entry : templateMap.values()) {
			entry.generate();
		}
		// generate code for the derived fields
		for (CodeGenerator entry : derivedTemplateMap.values()) {
			entry.generate();
		}
	}
	
	public static String getInterfaceName(String tableName) {
		return new StringBuilder().append(
				CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName))
				.append("Interface").toString();
	}
	
	public static String getDerivedInterfaceName(String tableName) {
		return new StringBuilder().append(
				CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName))
				.append("DerivedValueInterface").toString();
	}
	
	
	public static String getDefaultDerivedClassName(String tableName) {
		return new StringBuilder().append(
				CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tableName))
				.append("DefaultDerivedValues").toString();
	}
	
	public static String getGetterName(Field field) {
		FieldType type = field.getType();
		if (type instanceof PrimitiveFieldType &&
		    ((PrimitiveFieldType) type).getPrimitiveType() == PrimitiveType.BOOL) {
			return new StringBuilder().append("is").append(CaseFormat.LOWER_UNDERSCORE
					.to(CaseFormat.UPPER_CAMEL, field.getName())).toString();
		}
		return new StringBuilder().append("get").append(CaseFormat.LOWER_UNDERSCORE
				.to(CaseFormat.UPPER_CAMEL, field.getName())).toString();
	}
	
	public static String getSetterName(Field field) {
		return new StringBuilder().append("set").append(CaseFormat.LOWER_UNDERSCORE
				.to(CaseFormat.UPPER_CAMEL, field.getName())).toString();
	}
	
	public static String getDefaultValueGetterName(Field field) {
		return new StringBuilder().append("get").append(CaseFormat.LOWER_UNDERSCORE
				.to(CaseFormat.UPPER_CAMEL, field.getName())).append("DefaultValue").toString();
	}
	
	public static String getListSizeGetterName(Field field) {
		return new StringBuilder().append("get").append(CaseFormat.LOWER_UNDERSCORE
				.to(CaseFormat.UPPER_CAMEL, field.getName())).append("Size").toString();
	}
	
	/**
	 * Define the template for {@link InterfaceGenerator}.
	 * 
	 * @author tliu
	 * @author yqi
	 * @date Dec 15, 2014
	 */
	static class InterfaceTemplate {
		
		private final Field field;

		private String typeStr = "%s";
		
		public InterfaceTemplate(Field field) {
			//setup info
			this.field = field;

			// get fieldType
			typeStr = new JavaTypeConvertor().apply(field.getType());
		}
		
		public CodeTemplate getGetterTemplate() {
			String[] codeString = {
					"		public {fieldType} {getter}();",
				};
			
			//Create Code Template for different 
			CodeTemplate template = new CodeTemplate(codeString);
			template.fillFields("getter", getGetterName(field));
			template.fillFields("fieldType", this.typeStr);
			
			return template;
		}
		
		public CodeTemplate getSetterTemplate() {
			String[] codeString = {
					"		public void {setter}({fieldType} input);",
				};
			
			//Create Code Template for different 
			CodeTemplate template = new CodeTemplate(codeString);
			
			template.fillFields("setter", getSetterName(field));	
			template.fillFields("fieldType", this.typeStr);
			
			return template;
		}
		
		public CodeTemplate getDefaultValueTemplate() {
			String[] codeString = {
					"		public {fieldType} {fieldName}();",	
				};
			
			CodeTemplate template = new CodeTemplate(codeString);
			
			template.fillFields("fieldType", this.typeStr);
			template.fillFields("fieldName", getDefaultValueGetterName(field));
			
			return template;
		}
		
		public CodeTemplate getListSizeTemplate() {
			String[] codeString = {
					"		public int {getter}();",
				};
			
			CodeTemplate template = new CodeTemplate(codeString);
			
			template.fillFields("getter", getListSizeGetterName(field));
			
			return template;
		}
	}
}
