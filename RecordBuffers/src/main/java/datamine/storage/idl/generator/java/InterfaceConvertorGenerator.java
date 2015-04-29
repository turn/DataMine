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

public class InterfaceConvertorGenerator implements ElementVisitor, CodeGenerator {
	
	private Map<Table, CodeGenerator> templateMap = null;
	private CodeTemplate currentTemplate = null;
	
	private final String sourceDir;
	private final String nameSpace;
	private final String interfaceNameSpace;
	
	public InterfaceConvertorGenerator(String dir, String nameSpace, String interfacePakcage) {
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
				"public class {className} implements UnaryOperatorInterface<{interface}, {interface}> {",
				"",
				"	private RecordBuilderInterface builder;",
				"",
				"   public {interface}Convertor(RecordBuilderInterface builder) {",
				"   	this.builder = builder;",
				"	}",
				
				"	@Override",
				"	public {interface} apply({interface} input) {",
				"		{interface} output = builder.build({interface}.class);",
				"{fieldConvertor}",
				"		return output;",
				"	}",
				"",
				"}"
		};
		
		String[] importString = {
				"import " + this.interfaceNameSpace + ".*;",
				"import datamine.operator.UnaryOperatorInterface;",
				"import datamine.storage.api.RecordBuilderInterface;",
				"import java.util.*;"
		};
		
		CodeTemplate bodyTemplate = new CodeTemplate(classCode);
		CodeTemplate importTemplate = new CodeTemplate(importString);
		String className = InterfaceGenerator.getInterfaceName(table.getName()) + "Convertor";
		// TODO get the package name from the input
		JavaCodeGenerator javaCode = new JavaCodeGenerator(sourceDir,
				nameSpace, className, importTemplate, bodyTemplate);
		bodyTemplate.fillFields("interface", InterfaceGenerator.getInterfaceName(table.getName()));
		bodyTemplate.fillFields("className", className);
		templateMap.put(table, javaCode);
		currentTemplate = bodyTemplate;
	}

	@Override
	public void visit(Field field) {
				
		FieldType type = field.getType();
		CodeTemplate fieldCode = new FieldTemplateGenerator().apply(type);				
		
		fieldCode.fillFields("setter", InterfaceGenerator.getSetterName(field));
		fieldCode.fillFields("getter", InterfaceGenerator.getGetterName(field));
		
		//Add field change into body
		currentTemplate.fillFields("fieldConvertor", fieldCode);
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
		implements UnaryOperatorInterface<FieldType, CodeTemplate> {
		
		private CodeTemplate createPrimitiveFieldTypeTemplate(PrimitiveFieldType type) {
			final String[] primitive = {
					"		output.{setter}(input.{getter}());",
					""
				};
			return new CodeTemplate(primitive);
		}
		
		private CodeTemplate createGroupFieldTypeTemplate(GroupFieldType type) {
			final String[] struct = {
					"		{",
					"			{interface}Convertor convertor = new {interface}Convertor(builder);",
					"			if (input.{getter}() != null) {",
					"       		output.{setter}(convertor.apply(input.{getter}()));",
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
					"			List<{interface}> list = new ArrayList<{interface}>();",
					"			{interface}Convertor convertor = new {interface}Convertor(builder);",
					"			for ({interface} tuple: input.{getter}()) {",
					"				list.add(convertor.apply(tuple));",
					"			}",
					"			output.{setter}(list);",
					"		}"
			};
			
			CodeTemplate fieldCode = null;
			if (type.getType() == CollectionType.LIST) {
				FieldType elementType = type.getElementType();
				if (elementType instanceof PrimitiveFieldType) {
					fieldCode = createPrimitiveFieldTypeTemplate((PrimitiveFieldType) elementType);
				} else if (elementType instanceof GroupFieldType){
					//TODO change the definition of StructList to make the recursion possible
					fieldCode = new CodeTemplate(StructList);
					fieldCode.fillFields("interface", 
							InterfaceGenerator.getInterfaceName(
									((GroupFieldType) elementType).getGroupName()));	
				} else {
					throw new IllegalArgumentException("Not support more than 2 levels of nesting type");
				}
				
			} else {
				throw new IllegalArgumentException("Not support the type of " +
						((CollectionFieldType) type).getType());
			}
			return fieldCode;
		}
		
		@Override
		public CodeTemplate apply(FieldType input) {
			if (input instanceof PrimitiveFieldType) {
				return createPrimitiveFieldTypeTemplate((PrimitiveFieldType) input);
			} else if (input instanceof GroupFieldType) {
				return createGroupFieldTypeTemplate((GroupFieldType) input);
			} else if (input instanceof CollectionFieldType) {
				return createCollectionFieldTypeTemplate((CollectionFieldType) input);
			} else {
				throw new IllegalArgumentException("Not support the type of " + input);
			}
		}
	}
}
