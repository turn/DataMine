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

import org.testng.annotations.Test;

import datamine.storage.idl.Schema;
import datamine.storage.idl.json.JsonSchemaConvertor;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.idl.validate.exceptions.IdentityDuplicationException;
import datamine.storage.idl.validate.exceptions.IllegalDerivedFieldException;
import datamine.storage.idl.validate.exceptions.IllegalFieldIdentityException;
import datamine.storage.idl.validate.exceptions.IllegalFieldRestrictionException;
import datamine.storage.idl.validate.exceptions.IllegalNamingConversionException;
import datamine.storage.idl.validate.exceptions.IllegalTableVersionException;
import datamine.storage.idl.validate.exceptions.MultipleSortKeysException;
import datamine.storage.idl.validate.exceptions.NameDuplicationException;

public class SchemaValidationTest {

	/**
	 * Any table referred in the schema should be defined. 
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkUndefinedTable() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"},\r\n" + 
				"		 {\"id\": 5,	\"name\": \"nested_table_column\",          \"type\": \"List:first_level_nested_table\", \"hasLargeList\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	/**
	 * The table version should be positive.
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = IllegalTableVersionException.class)
	public void checkTableVersion() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" +
				"      \"version\": \"-1\",\r\n" +
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	/**
	 * Any (non-derived) field should have unique field id. 
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = IdentityDuplicationException.class)
	public void checkFieldDuplicateId() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" +
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_required_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 3,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	/**
	 * The (non-derived) field ID should be an integer in the ascending order. 
	 * The difference of any two adjacent (non-derived) fields should be always 1. 
	 *   
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = IllegalFieldIdentityException.class)
	public void checkFieldFalseId() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 5,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	/**
	 * The name of field should be aligned with the naming conversion in Java
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = IllegalNamingConversionException.class)
	public void checkInvalidName() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"_2243+!#long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	/**
	 * No any two fields have the same name. 
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = NameDuplicationException.class)
	public void checkDuplicateName() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"long_required_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	/**
	 * No more than one primitive field can be sort-key. 
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = MultipleSortKeysException.class)
	public void checkSortKey() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isDesSortKey\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\"}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	/**
	 * The field id of the derived field should be always 0. 
	 *  
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = IllegalFieldIdentityException.class)
	public void checkFieldId() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\", 	\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	/**
	 * The derived field should be primitive
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = IllegalDerivedFieldException.class)
	public void checkDerivedFieldMustBePrimitive() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 0,	\"name\": \"boolean_column\",      			\"type\": \"List:Boolean\", 	\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	/**
	 * The derived field cannot be required
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = IllegalDerivedFieldException.class)
	public void checkDerivedFieldMustBeOptional() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 0,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\",   	\"isRequired\": true, 		\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	/**
	 * Derived field cannot be sorted
	 * @throws AbstractValidationException
	 */
	@Test(expectedExceptions = IllegalDerivedFieldException.class)
	public void checkDerivedFieldCannotBeSortKey() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 0,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\", 	\"isRequired\": true, \"isDesSortKey\": true, 	\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	@Test(expectedExceptions = IllegalDerivedFieldException.class)
	public void checkDerivedFieldCannotBeFrequentlyUsed() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"},\r\n" + 
				"        {\"id\": 0,	\"name\": \"boolean_column\",      			\"type\": \"Boolean\", 	\"default\": \"false\", \"isFrequentlyUsed\": true, 	\"isDerived\": true}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	@Test(expectedExceptions = IllegalFieldRestrictionException.class)
	public void checkNonCollectionFieldCannotBeLargeList() throws AbstractValidationException {
		String json_str = 
				"{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"main_table\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,	\"name\": \"long_required_column\",			\"type\": \"Long\",   	\"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,	\"name\": \"int_sorted_column\",         	\"type\": \"Integer\", 	\"isRequired\": true, \"isDesSortKey\": true, \"hasLargeList\": true},\r\n" + 
				"        {\"id\": 3,	\"name\": \"byte_column\",      			\"type\": \"Byte\",  	\"default\": \"-1\"}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
}
