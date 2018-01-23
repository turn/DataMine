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
package datamine.storage.idl.validate;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import datamine.storage.idl.Schema;
import datamine.storage.idl.json.JsonSchemaConvertor;
import datamine.storage.idl.validate.SchemaEvolutionValidation;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.idl.validate.exceptions.FieldConstraintModifiedInSchemaEvolutionException;
import datamine.storage.idl.validate.exceptions.FieldDefaultValueModifiedInSchemaEvolutionException;
import datamine.storage.idl.validate.exceptions.FieldDeletionInSchemaEvolutionException;
import datamine.storage.idl.validate.exceptions.FieldTypeModifiedInSchemaEvolutionException;

public class SchemaEvolutionValidationTest {

	private Schema currentSchema = null;
	
	@BeforeMethod
	private void prepareRecord() {
		final String json_schema = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"second_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"byte_required_column\",			\"type\": \"Byte\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"boolean_list_column\",      		\"type\": \"List:Boolean\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"first_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, 	\"name\": \"int_required_column\", 			\"type\": \"Integer\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", \"default\": \"Unknown\", 	\"isDerived\": true}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"   {\r\n" + 
				"      \"table\": \"struct_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"},\r\n" + 
				"		{\"id\": 5,	\"name\": \"short_column\",      			\"type\": \"Short\", 	\"default\": \"0\"},\r\n" + 
				"		{\"id\": 6,	\"name\": \"float_column\",      			\"type\": \"Float\", 	\"default\": \"0.0\"},\r\n" + 
				"		{\"id\": 7,	\"name\": \"double_column\",      			\"type\": \"Double\", 	\"default\": \"0.001\"},\r\n" + 
				"		{\"id\": 8,	\"name\": \"string_column\",      			\"type\": \"String\", 	\"default\": \"Unknown\"},\r\n" + 
				"		{\"id\": 9,	\"name\": \"binary_column\",     			\"type\": \"Binary\"},\r\n" + 
				"		{\"id\": 10,	\"name\": \"nested_table_column\",          \"type\": \"List:first_level_nested_table\", \"hasLargeList\": true},\r\n" + 
				"		{\"id\": 11,	\"name\": \"struct_column\",                \"type\": \"struct_table\"},\r\n" + 
				"		{\"id\": 12, 	\"name\": \"int_list_column\",     			\"type\": \"List:Integer\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", 	\"default\": \"Unknown\", 	\"isDerived\": true},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"int_derived_column\",     		\"type\": \"Integer\", 	\"default\": \"0\", 		\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}\r\n";
			currentSchema = new JsonSchemaConvertor().apply(json_schema);
	}
	
	/**
	 * Any existing table cannot be removed (e.g., struct_table).
	 *  
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkNextSchema() throws AbstractValidationException {
		final String json_schema = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"second_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"byte_required_column\",			\"type\": \"Byte\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"boolean_list_column\",      		\"type\": \"List:Boolean\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"first_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, 	\"name\": \"int_required_column\", 			\"type\": \"Integer\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", \"default\": \"Unknown\", 	\"isDerived\": true}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"},\r\n" + 
				"		{\"id\": 5,	\"name\": \"short_column\",      			\"type\": \"Short\", 	\"default\": \"0\"},\r\n" + 
				"		{\"id\": 6,	\"name\": \"float_column\",      			\"type\": \"Float\", 	\"default\": \"0.0\"},\r\n" + 
				"		{\"id\": 7,	\"name\": \"double_column\",      			\"type\": \"Double\", 	\"default\": \"0.001\"},\r\n" + 
				"		{\"id\": 8,	\"name\": \"string_column\",      			\"type\": \"String\", 	\"default\": \"Unknown\"},\r\n" + 
				"		{\"id\": 9,	\"name\": \"binary_column\",     			\"type\": \"Binary\"},\r\n" + 
				"		{\"id\": 10,	\"name\": \"nested_table_column\",          \"type\": \"List:first_level_nested_table\", \"hasLargeList\": true},\r\n" + 
				"		{\"id\": 11,	\"name\": \"struct_column\",                \"type\": \"struct_table\"},\r\n" + 
				"		{\"id\": 12, 	\"name\": \"int_list_column\",     			\"type\": \"List:Integer\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", 	\"default\": \"Unknown\", 	\"isDerived\": true},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"int_derived_column\",     		\"type\": \"Integer\", 	\"default\": \"0\", 		\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}\r\n";
		
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate =
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	/**
	 * Cannot delete a non-derived field (e.g., int_list_column) 
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = FieldDeletionInSchemaEvolutionException.class)
	public void checkFieldSchema() throws AbstractValidationException {
		final String json_schema = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"second_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"byte_required_column\",			\"type\": \"Byte\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"boolean_list_column\",      		\"type\": \"List:Boolean\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"first_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, 	\"name\": \"int_required_column\", 			\"type\": \"Integer\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", \"default\": \"Unknown\", 	\"isDerived\": true}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"   {\r\n" + 
				"      \"table\": \"struct_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"},\r\n" + 
				"		{\"id\": 5,	\"name\": \"short_column\",      			\"type\": \"Short\", 	\"default\": \"0\"},\r\n" + 
				"		{\"id\": 6,	\"name\": \"float_column\",      			\"type\": \"Float\", 	\"default\": \"0.0\"},\r\n" + 
				"		{\"id\": 7,	\"name\": \"double_column\",      			\"type\": \"Double\", 	\"default\": \"0.001\"},\r\n" + 
				"		{\"id\": 8,	\"name\": \"string_column\",      			\"type\": \"String\", 	\"default\": \"Unknown\"},\r\n" + 
				"		{\"id\": 9,	\"name\": \"binary_column\",     			\"type\": \"Binary\"},\r\n" + 
				"		{\"id\": 10,	\"name\": \"nested_table_column\",          \"type\": \"List:first_level_nested_table\", \"hasLargeList\": true},\r\n" + 
				"		{\"id\": 11,	\"name\": \"struct_column\",                \"type\": \"struct_table\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", 	\"default\": \"Unknown\", 	\"isDerived\": true},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"int_derived_column\",     		\"type\": \"Integer\", 	\"default\": \"0\", 		\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}\r\n";
				
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate =
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	/**
	 * Cannot change the type of a non-derived field (e.g., int_sorted_column)
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = FieldTypeModifiedInSchemaEvolutionException.class)
	public void checkFieldTypeChangedSchema() throws AbstractValidationException {
		final String json_schema = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"second_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"byte_required_column\",			\"type\": \"Byte\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"boolean_list_column\",      		\"type\": \"List:Boolean\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"first_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, 	\"name\": \"int_required_column\", 			\"type\": \"Integer\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", \"default\": \"Unknown\", 	\"isDerived\": true}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"   {\r\n" + 
				"      \"table\": \"struct_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Long\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"},\r\n" + 
				"		{\"id\": 5,	\"name\": \"short_column\",      			\"type\": \"Short\", 	\"default\": \"0\"},\r\n" + 
				"		{\"id\": 6,	\"name\": \"float_column\",      			\"type\": \"Float\", 	\"default\": \"0.0\"},\r\n" + 
				"		{\"id\": 7,	\"name\": \"double_column\",      			\"type\": \"Double\", 	\"default\": \"0.001\"},\r\n" + 
				"		{\"id\": 8,	\"name\": \"string_column\",      			\"type\": \"String\", 	\"default\": \"Unknown\"},\r\n" + 
				"		{\"id\": 9,	\"name\": \"binary_column\",     			\"type\": \"Binary\"},\r\n" + 
				"		{\"id\": 10,	\"name\": \"nested_table_column\",          \"type\": \"List:first_level_nested_table\", \"hasLargeList\": true},\r\n" + 
				"		{\"id\": 11,	\"name\": \"struct_column\",                \"type\": \"struct_table\"},\r\n" + 
				"		{\"id\": 12, 	\"name\": \"int_list_column\",     			\"type\": \"List:Integer\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", 	\"default\": \"Unknown\", 	\"isDerived\": true},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"int_derived_column\",     		\"type\": \"Integer\", 	\"default\": \"0\", 		\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}\r\n";
		
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate =
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	/**
	 * Cannot change the default value of a non-derived field (e.g., short_column)
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = FieldDefaultValueModifiedInSchemaEvolutionException.class)
	public void checkFieldDefaultValueChangedSchema() throws AbstractValidationException {
		final String json_schema = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"second_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"byte_required_column\",			\"type\": \"Byte\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"boolean_list_column\",      		\"type\": \"List:Boolean\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"first_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, 	\"name\": \"int_required_column\", 			\"type\": \"Integer\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", \"default\": \"Unknown\", 	\"isDerived\": true}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"   {\r\n" + 
				"      \"table\": \"struct_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"},\r\n" + 
				"		{\"id\": 5,	\"name\": \"short_column\",      			\"type\": \"Short\", 	\"default\": \"11\"},\r\n" + 
				"		{\"id\": 6,	\"name\": \"float_column\",      			\"type\": \"Float\", 	\"default\": \"0.0\"},\r\n" + 
				"		{\"id\": 7,	\"name\": \"double_column\",      			\"type\": \"Double\", 	\"default\": \"0.001\"},\r\n" + 
				"		{\"id\": 8,	\"name\": \"string_column\",      			\"type\": \"String\", 	\"default\": \"Unknown\"},\r\n" + 
				"		{\"id\": 9,	\"name\": \"binary_column\",     			\"type\": \"Binary\"},\r\n" + 
				"		{\"id\": 10,	\"name\": \"nested_table_column\",          \"type\": \"List:first_level_nested_table\", \"hasLargeList\": true},\r\n" + 
				"		{\"id\": 11,	\"name\": \"struct_column\",                \"type\": \"struct_table\"},\r\n" + 
				"		{\"id\": 12, 	\"name\": \"int_list_column\",     			\"type\": \"List:Integer\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", 	\"default\": \"Unknown\", 	\"isDerived\": true},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"int_derived_column\",     		\"type\": \"Integer\", 	\"default\": \"0\", 		\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}\r\n";
		
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate =
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	/**
	 * The field (e.g.,int_sorted_column) cannot change its sorting direction once it is defined
	 * 
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = FieldConstraintModifiedInSchemaEvolutionException.class)
	public void checkFieldConstraintChangedSchema1() throws AbstractValidationException {
		final String json_schema = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"second_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"byte_required_column\",			\"type\": \"Byte\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"boolean_list_column\",      		\"type\": \"List:Boolean\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"first_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, 	\"name\": \"int_required_column\", 			\"type\": \"Integer\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", \"default\": \"Unknown\", 	\"isDerived\": true}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"   {\r\n" + 
				"      \"table\": \"struct_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isAscSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"},\r\n" + 
				"		{\"id\": 5,	\"name\": \"short_column\",      			\"type\": \"Short\", 	\"default\": \"0\"},\r\n" + 
				"		{\"id\": 6,	\"name\": \"float_column\",      			\"type\": \"Float\", 	\"default\": \"0.0\"},\r\n" + 
				"		{\"id\": 7,	\"name\": \"double_column\",      			\"type\": \"Double\", 	\"default\": \"0.001\"},\r\n" + 
				"		{\"id\": 8,	\"name\": \"string_column\",      			\"type\": \"String\", 	\"default\": \"Unknown\"},\r\n" + 
				"		{\"id\": 9,	\"name\": \"binary_column\",     			\"type\": \"Binary\"},\r\n" + 
				"		{\"id\": 10,	\"name\": \"nested_table_column\",          \"type\": \"List:first_level_nested_table\", \"hasLargeList\": true},\r\n" + 
				"		{\"id\": 11,	\"name\": \"struct_column\",                \"type\": \"struct_table\"},\r\n" + 
				"		{\"id\": 12, 	\"name\": \"int_list_column\",     			\"type\": \"List:Integer\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", 	\"default\": \"Unknown\", 	\"isDerived\": true},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"int_derived_column\",     		\"type\": \"Integer\", 	\"default\": \"0\", 		\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}\r\n";
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate =
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	/**
	 * Cannot change a field from optional into required (e.g., boolean_list_column)
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = FieldConstraintModifiedInSchemaEvolutionException.class)
	public void checkFieldConstraintChangedSchema2() throws AbstractValidationException {
		final String json_schema = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"second_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"byte_required_column\",			\"type\": \"Byte\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"boolean_list_column\",      		\"type\": \"List:Boolean\",		\"isRequired\": true}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"first_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, 	\"name\": \"int_required_column\", 			\"type\": \"Integer\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", \"default\": \"Unknown\", 	\"isDerived\": true}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"   {\r\n" + 
				"      \"table\": \"struct_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"},\r\n" + 
				"		{\"id\": 5,	\"name\": \"short_column\",      			\"type\": \"Short\", 	\"default\": \"0\"},\r\n" + 
				"		{\"id\": 6,	\"name\": \"float_column\",      			\"type\": \"Float\", 	\"default\": \"0.0\"},\r\n" + 
				"		{\"id\": 7,	\"name\": \"double_column\",      			\"type\": \"Double\", 	\"default\": \"0.001\"},\r\n" + 
				"		{\"id\": 8,	\"name\": \"string_column\",      			\"type\": \"String\", 	\"default\": \"Unknown\"},\r\n" + 
				"		{\"id\": 9,	\"name\": \"binary_column\",     			\"type\": \"Binary\"},\r\n" + 
				"		{\"id\": 10,	\"name\": \"nested_table_column\",          \"type\": \"List:first_level_nested_table\", \"hasLargeList\": true},\r\n" + 
				"		{\"id\": 11,	\"name\": \"struct_column\",                \"type\": \"struct_table\"},\r\n" + 
				"		{\"id\": 12, 	\"name\": \"int_list_column\",     			\"type\": \"List:Integer\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", 	\"default\": \"Unknown\", 	\"isDerived\": true},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"int_derived_column\",     		\"type\": \"Integer\", 	\"default\": \"0\", 		\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}\r\n";
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate =
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	/**
	 * Cannot make a field from sorting into non-sorting (e.g., int_sorted_column)
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = FieldConstraintModifiedInSchemaEvolutionException.class)
	public void checkFieldConstraintChangedSchema3() throws AbstractValidationException {
		final String json_schema = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"second_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"byte_required_column\",			\"type\": \"Byte\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"boolean_list_column\",      		\"type\": \"List:Boolean\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"first_level_nested_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, 	\"name\": \"int_required_column\", 			\"type\": \"Integer\",		\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", \"default\": \"Unknown\", 	\"isDerived\": true}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"   {\r\n" + 
				"      \"table\": \"struct_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"nested_table_column\",			\"type\": \"List:second_level_nested_table\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"},\r\n" + 
				"		{\"id\": 5,	\"name\": \"short_column\",      			\"type\": \"Short\", 	\"default\": \"0\"},\r\n" + 
				"		{\"id\": 6,	\"name\": \"float_column\",      			\"type\": \"Float\", 	\"default\": \"0.0\"},\r\n" + 
				"		{\"id\": 7,	\"name\": \"double_column\",      			\"type\": \"Double\", 	\"default\": \"0.001\"},\r\n" + 
				"		{\"id\": 8,	\"name\": \"string_column\",      			\"type\": \"String\", 	\"default\": \"Unknown\"},\r\n" + 
				"		{\"id\": 9,	\"name\": \"binary_column\",     			\"type\": \"Binary\"},\r\n" + 
				"		{\"id\": 10,	\"name\": \"nested_table_column\",          \"type\": \"List:first_level_nested_table\", \"hasLargeList\": true},\r\n" + 
				"		{\"id\": 11,	\"name\": \"struct_column\",                \"type\": \"struct_table\"},\r\n" + 
				"		{\"id\": 12, 	\"name\": \"int_list_column\",     			\"type\": \"List:Integer\"},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"string_derived_column\",     	\"type\": \"String\", 	\"default\": \"Unknown\", 	\"isDerived\": true},\r\n" + 
				"		{\"id\": 0, 	\"name\": \"int_derived_column\",     		\"type\": \"Integer\", 	\"default\": \"0\", 		\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}\r\n";
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate =
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
}
