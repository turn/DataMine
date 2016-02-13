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
package datamine.storage.idl.generator.java;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import datamine.operator.UnaryOperatorInterface;
import datamine.storage.idl.ElementVisitor;
import datamine.storage.idl.Field;
import datamine.storage.idl.Schema;
import datamine.storage.idl.Table;
import datamine.storage.idl.generator.CodeGenerator;
import datamine.storage.idl.generator.CodeTemplate;
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.CollectionType;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.idl.type.PrimitiveFieldType;

public class InterfaceContentPrinterGenerator implements ElementVisitor, CodeGenerator {
	
	private Map<Table, CodeGenerator> templateMap = null;
	private CodeTemplate currentTemplate = null;
	
	private final String sourceDir;
	private final String nameSpace;
	private final String interfaceNameSpace;
	
	public InterfaceContentPrinterGenerator(String dir, String nameSpace, String interfacePakcage) {
		this.sourceDir = dir;
		this.nameSpace = nameSpace;
		this.interfaceNameSpace = interfacePakcage;
	}
	
	@Override
	public void visit(Schema schema) {
		templateMap = new HashMap<Table, CodeGenerator>();
	}

	@Override
	public void visit(Table table) {
		String[] classCode = {
				"public class {className} implements UnaryOperatorInterface<{interface}, String> {",
				"",
				"	@Override",
				"	public String apply({interface} input) {",
				"		StringBuffer out = new StringBuffer();",
				"		out.append(\"{\\n\");",
				"{printFields}",
				"		out.append(\"}\");",
				"		return out.toString();",
				"	}",
				"",
				"}"
		};
		
		String[] importString = {
				"import " + this.interfaceNameSpace + ".*;",
				"import datamine.operator.UnaryOperatorInterface;",
		};
		
		CodeTemplate bodyTemplate = new CodeTemplate(classCode);
		CodeTemplate importTemplate = new CodeTemplate(importString);
		String className = InterfaceGenerator.getInterfaceName(table.getName()) + "ContentPrinter";
		JavaCodeGenerator javaCode = new JavaCodeGenerator(sourceDir,
				nameSpace, className, importTemplate, bodyTemplate);
		bodyTemplate.fillFields("interface", InterfaceGenerator.getInterfaceName(table.getName()));
		bodyTemplate.fillFields("className", className);
		templateMap.put(table, javaCode);
		currentTemplate = bodyTemplate;
	}

	@Override
	public void visit(Field field) {
				
		CodeTemplate fieldCode = new FieldTemplateGenerator().apply(field);				
		fieldCode.fillFields("getter", InterfaceGenerator.getGetterName(field));
		
		//Add field change into body
		currentTemplate.fillFields("printFields", fieldCode);
	}

	@Override
	public void generate() {
		for (Entry<Table, CodeGenerator> entry : templateMap.entrySet()) {
			entry.getValue().generate();
		}
	}
	
	
	/**
	 * The class, {@link FieldTemplateGenerator} creates the template for the input
	 * field type.
	 * 
	 * @author yqi
	 * @date Dec 16, 2014
	 */
	static class FieldTemplateGenerator 
		implements UnaryOperatorInterface<Field, CodeTemplate> {
		
		private FieldType type;
		private String name;

		private CodeTemplate createPrimitiveFieldTypeTemplate(PrimitiveFieldType type) {
			final String[] primitive = {
					"\t\tout.append(\""+name+" = \").append(input.{getter}()).append(\"\\n\");",
				};
			return new CodeTemplate(primitive);
		}
		
		private CodeTemplate createGroupFieldTypeTemplate(GroupFieldType type) {
			final String[] struct = {
					"		{",
					"			{interface}ContentPrinter printer = new {interface}ContentPrinter();",
					"			if (input.{getter}() != null) {",
					"       		out.append(\""+name+" = \").append(printer.apply(input.{getter}())).append(\"\\n\");",
					"			}",
					"		}",
				};
			
			CodeTemplate fieldCode = null;
			fieldCode = new CodeTemplate(struct);
			fieldCode.fillFields("interface", 
					InterfaceGenerator.getInterfaceName(type.getGroupName()));	
			return fieldCode;
		}
		
		private CodeTemplate createCollectionFieldTypeTemplate(CollectionFieldType type) {
			final String[] StructList = {
					"		{",
					"			{interface}ContentPrinter printer = new {interface}ContentPrinter();",
					"			out.append(\""+name+" = \").append(\"[\").append(\"\\n\");",
					"			for ({interface} tuple: input.{getter}()) {",
					"				out.append(printer.apply(tuple)).append(\"\\n\");",
					"			}",
					"			out.append(\"]\").append(\"\\n\");",
					"		}"
			};
			
			CodeTemplate fieldCode = null;
			if (type.getCollectionType() == CollectionType.LIST) {
				FieldType elementType = type.getElementType();
				if (elementType instanceof PrimitiveFieldType) {
					fieldCode = createPrimitiveFieldTypeTemplate((PrimitiveFieldType) elementType);
				} else if (elementType instanceof GroupFieldType){
					fieldCode = new CodeTemplate(StructList);
					fieldCode.fillFields("interface", 
							InterfaceGenerator.getInterfaceName(
									((GroupFieldType) elementType).getGroupName()));	
				} else {
					throw new IllegalArgumentException("Not support more than 2 levels of nesting type");
				}
				
			} else {
				throw new IllegalArgumentException("Not support the type of " +
						((CollectionFieldType) type).getCollectionType());
			}
			return fieldCode;
		}
		
		@Override
		public CodeTemplate apply(Field input) {
			type = input.getType();
			name = input.getName();
			if (type instanceof PrimitiveFieldType) {
				return createPrimitiveFieldTypeTemplate((PrimitiveFieldType) type);
			} else if (type instanceof GroupFieldType) {
				return createGroupFieldTypeTemplate((GroupFieldType) type);
			} else if (type instanceof CollectionFieldType) {
				return createCollectionFieldTypeTemplate((CollectionFieldType) type);
			} else {
				throw new IllegalArgumentException("Not support the type of " + type);
			}
		}
	}
}
