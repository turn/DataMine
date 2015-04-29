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
package datamine.storage.idl.validate;

import org.testng.annotations.Test;

import datamine.storage.idl.Schema;
import datamine.storage.idl.json.JsonSchemaConvertor;
import datamine.storage.idl.validate.SchemaValidation;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.idl.validate.exceptions.IdentityDuplicationException;
import datamine.storage.idl.validate.exceptions.IllegalFieldDefaultValueException;
import datamine.storage.idl.validate.exceptions.IllegalFieldIdentityException;
import datamine.storage.idl.validate.exceptions.IllegalNamingConversionException;
import datamine.storage.idl.validate.exceptions.IllegalTableVersionException;
import datamine.storage.idl.validate.exceptions.MultipleSortKeysException;
import datamine.storage.idl.validate.exceptions.NameDuplicationException;

public class SchemaValidationTest {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkUndefinedTable() throws AbstractValidationException {
		String json_str = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"id\",\"type\": \"Integer\",\"isRequired\": true},\n        {\"id\": 2,\"name\": \"rules\",      \"type\": \"List:attribution_result_rule\", \"isRequired\": true}\n      ]\n    }\n\t]\n}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	@Test(expectedExceptions = IllegalTableVersionException.class)
	public void checkTableVersion() throws AbstractValidationException {
		String json_str = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result\",\n\t  \"version\": \"-1\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"id\",\"type\": \"Integer\",\"isRequired\": true}\n      ]\n    }\n\t]\n}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	@Test(expectedExceptions = IdentityDuplicationException.class)
	public void checkFieldDuplicateId() throws AbstractValidationException {
		String json_str = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result_rule\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 2,\"name\": \"key\",      \"type\": \"String\", \"default\": \"Unknown\"}\n      ]\n    }\n    ]\n}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	@Test(expectedExceptions = IllegalFieldIdentityException.class)
	public void checkFieldFalseId() throws AbstractValidationException {
		String json_str = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result_rule\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 5,\"name\": \"key\",      \"type\": \"String\", \"default\": \"Unknown\"}\n      ]\n    }\n    ]\n}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	@Test(expectedExceptions = IllegalNamingConversionException.class)
	public void checkInvalidName() throws AbstractValidationException {
		String json_str = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result_rule\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"run num\",    \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 3,\"name\": \"key\",      \"type\": \"String\", \"default\": \"Unknown\"}\n      ]\n    }\n    ]\n}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	@Test(expectedExceptions = NameDuplicationException.class)
	public void checkDuplicateName() throws AbstractValidationException {
		String json_str = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result_rule\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"key\",    \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 3,\"name\": \"key\",      \"type\": \"String\", \"default\": \"Unknown\"}\n      ]\n    }\n    ]\n}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
	@Test(expectedExceptions = MultipleSortKeysException.class)
	public void checkSortKey() throws AbstractValidationException {
		String json_str = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result_rule\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true, \"isSortKey\": true},\n        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"isRequired\": true, \"isSortKey\": true},\n        {\"id\": 3,\"name\": \"key\",      \"type\": \"String\", \"default\": \"Unknown\"}\n      ]\n    }\n    ]\n}";
		Schema schema = new JsonSchemaConvertor().apply(json_str);
		new SchemaValidation().check(schema);
	}
	
//	@Test(expectedExceptions = IllegalFieldDefaultValueException.class)
//	public void checkFieldDefaultValue() throws AbstractValidationException {
//		String json_str = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result_rule\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"value\",      \"type\": \"Integer\", \"default\": 0.1},\n        {\"id\": 3,\"name\": \"key\",      \"type\": \"String\", \"default\": \"Unknown\"}\n      ]\n    }\n    ]\n}";
//		Schema schema = new JsonSchemaConvertor().apply(json_str);
//		new SchemaValidation().check(schema);
//	}
}
