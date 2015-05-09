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

import java.io.File;
import java.io.IOException;

import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import datamine.storage.idl.Schema;
import datamine.storage.idl.generator.metadata.MetadataPackageToSchema;
import datamine.storage.idl.json.JsonSchemaConvertor;
import datamine.storage.idl.validate.SchemaEvolutionValidation;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.idl.validate.exceptions.FieldConstraintModifiedInSchemaEvolutionException;
import datamine.storage.idl.validate.exceptions.FieldDefaultValueModifiedInSchemaEvolutionException;
import datamine.storage.idl.validate.exceptions.FieldDeletionInSchemaEvolutionException;
import datamine.storage.idl.validate.exceptions.FieldTypeModifiedInSchemaEvolutionException;

public class SchemaEvolutionValidationTest {

	private final Schema currentSchema = 
			new MetadataPackageToSchema().apply("datamine.storage.recordbuffers.example");
	
	@Test
	public void validate() throws IOException, AbstractValidationException {
		File schemaPath = new File("src/test/resources/SimpleSchema.json");
		Schema nextSchema = new JsonSchemaConvertor().apply(
				Files.toString(schemaPath, Charsets.UTF_8));
		SchemaEvolutionValidation validate = 
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void checkNextSchema() throws AbstractValidationException {
		String json_schema = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result_rule\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"}\n      ]\n    },\n    {\n      \"table\": \"attribution_result\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"id\",\"type\": \"Integer\",\"isRequired\": true},\n        {\"id\": 2,\"name\": \"rules\",      \"type\": \"List:attribution_result_rule\", \"isRequired\": true}\n      ]\n    },\n    {\n      \"table\": \"impression\",\n      \"fields\": [\n        {\"id\": 1, \"name\": \"media_provider_id\", \"type\": \"Integer\",\"isRequired\": true},\n        {\"id\": 2,\"name\": \"mp_tpt_category_id\",\"type\": \"Short\",  \"default\": \"-1\"},\n        {\"id\": 3,\"name\": \"truncated_url\",     \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 4,\"name\": \"bid\",         \"type\": \"Boolean\",  \"default\": \"true\"},\n        {\"id\": 5,\"name\": \"bid_type\",          \"type\": \"Byte\",   \"default\": \"-1\"},\n        {\"id\": 6,\"name\": \"attribution_results\",\"type\": \"List:attribution_result\"},\n        {\"id\": 7,\"name\": \"allowed_ad_formats\",\"type\": \"Long\",   \"default\": \"-1\"},\n\t\t{\"id\": 8,\"name\": \"cost\",\"type\": \"Double\",   \"default\": \"0\"}\n\t\t]\n    },\n    {\n      \"table\": \"provider_user_id\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"provider_type\",          \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"provider_id\",            \"type\": \"Integer\",\"isRequired\": true}\n      ]\n    },\n    {\n      \"table\": \"analytical_user_profile\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"user_id\",                 \"type\": \"Long\",   \"isRequired\": true, \"isFrequentlyUsed\": true},\n        {\"id\": 2,\"name\": \"version\",         \"type\": \"Byte\", \"isRequired\": true, \"isDesSortKey\": true},\n        {\"id\": 3,\"name\": \"resolution\",      \"type\": \"Short\",  \"default\": \"-1\"},\n        {\"id\": 4,\"name\": \"os_version\",      \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 5,\"name\": \"impressions\",            \"type\": \"List:impression\"},\n        {\"id\": 6,\"name\": \"id_maps\",                \"type\": \"id_map\"},\n\t\t{\"id\": 7, \"name\": \"time_list\",     \"type\": \"List:Integer\"}\n      ]\n    }\n  ]\n}";
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate = 
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	@Test(expectedExceptions = FieldDeletionInSchemaEvolutionException.class)
	public void checkFieldSchema() throws AbstractValidationException {
		String json_schema = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result_rule\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"}\n      ]\n    },\n    {\n      \"table\": \"attribution_result\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"id\",\"type\": \"Integer\",\"isRequired\": true},\n        {\"id\": 2,\"name\": \"rules\",      \"type\": \"List:attribution_result_rule\", \"isRequired\": true}\n      ]\n    },\n    {\n      \"table\": \"impression\",\n      \"fields\": [\n        {\"id\": 1, \"name\": \"media_provider_id\", \"type\": \"Integer\",\"isRequired\": true},\n        {\"id\": 2,\"name\": \"mp_tpt_category_id\",\"type\": \"Short\",  \"default\": \"-1\"},\n        {\"id\": 3,\"name\": \"truncated_url\",     \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 4,\"name\": \"bid\",         \"type\": \"Boolean\",  \"default\": \"true\"},\n        {\"id\": 5,\"name\": \"bid_type\",          \"type\": \"Byte\",   \"default\": \"-1\"},\n        {\"id\": 6,\"name\": \"attribution_results\",\"type\": \"List:attribution_result\"},\n        {\"id\": 7,\"name\": \"allowed_ad_formats\",\"type\": \"Long\",   \"default\": \"-1\"}\n\t\t]\n    },\n    {\n      \"table\": \"provider_user_id\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"provider_type\",          \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"provider_id\",            \"type\": \"Integer\",\"isRequired\": true}\n      ]\n    },\n    {\n      \"table\": \"id_map\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"media_provider_ids\",\"type\": \"List:provider_user_id\"}\n      ]\n    },\n    {\n      \"table\": \"analytical_user_profile\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"user_id\",                 \"type\": \"Long\",   \"isRequired\": true, \"isFrequentlyUsed\": true},\n        {\"id\": 2,\"name\": \"version\",         \"type\": \"Byte\", \"isRequired\": true, \"isDesSortKey\": true},\n        {\"id\": 3,\"name\": \"resolution\",      \"type\": \"Short\",  \"default\": \"-1\"},\n        {\"id\": 4,\"name\": \"os_version\",      \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 5,\"name\": \"impressions\",            \"type\": \"List:impression\"},\n        {\"id\": 6,\"name\": \"id_maps\",                \"type\": \"id_map\"},\n\t\t{\"id\": 7, \"name\": \"time_list\",     \"type\": \"List:Integer\"}\n      ]\n    }\n  ]\n}";
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate = 
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	@Test(expectedExceptions = FieldTypeModifiedInSchemaEvolutionException.class)
	public void checkFieldTypeChangedSchema() throws AbstractValidationException {
		String json_schema = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result_rule\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"}\n      ]\n    },\n    {\n      \"table\": \"attribution_result\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"id\",\"type\": \"Integer\",\"isRequired\": true},\n        {\"id\": 2,\"name\": \"rules\",      \"type\": \"List:attribution_result_rule\", \"isRequired\": true}\n      ]\n    },\n    {\n      \"table\": \"impression\",\n      \"fields\": [\n        {\"id\": 1, \"name\": \"media_provider_id\", \"type\": \"Float\",\"isRequired\": true},\n        {\"id\": 2,\"name\": \"mp_tpt_category_id\",\"type\": \"Short\",  \"default\": \"-1\"},\n        {\"id\": 3,\"name\": \"truncated_url\",     \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 4,\"name\": \"bid\",         \"type\": \"Boolean\",  \"default\": \"true\"},\n        {\"id\": 5,\"name\": \"bid_type\",          \"type\": \"Byte\",   \"default\": \"-1\"},\n        {\"id\": 6,\"name\": \"attribution_results\",\"type\": \"List:attribution_result\"},\n        {\"id\": 7,\"name\": \"allowed_ad_formats\",\"type\": \"Long\",   \"default\": \"-1\"},\n\t\t{\"id\": 8,\"name\": \"cost\",\"type\": \"Double\",   \"default\": \"0\"}\n\t\t]\n    },\n    {\n      \"table\": \"provider_user_id\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"provider_type\",          \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"provider_id\",            \"type\": \"Integer\",\"isRequired\": true}\n      ]\n    },\n    {\n      \"table\": \"id_map\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"media_provider_ids\",\"type\": \"List:provider_user_id\"}\n      ]\n    },\n    {\n      \"table\": \"analytical_user_profile\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"user_id\",                 \"type\": \"Long\",   \"isRequired\": true, \"isFrequentlyUsed\": true},\n        {\"id\": 2,\"name\": \"version\",         \"type\": \"Byte\", \"isRequired\": true, \"isDesSortKey\": true},\n        {\"id\": 3,\"name\": \"resolution\",      \"type\": \"Short\",  \"default\": \"-1\"},\n        {\"id\": 4,\"name\": \"os_version\",      \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 5,\"name\": \"impressions\",            \"type\": \"List:impression\"},\n        {\"id\": 6,\"name\": \"id_maps\",                \"type\": \"id_map\"},\n\t\t{\"id\": 7, \"name\": \"time_list\",     \"type\": \"List:Integer\"}\n      ]\n    }\n  ]\n}";
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate = 
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	@Test(expectedExceptions = FieldDefaultValueModifiedInSchemaEvolutionException.class)
	public void checkFieldDefaultValueChangedSchema() throws AbstractValidationException {
		String json_schema = "{\n  \"schema\": \"simple_schema\",\n  \"table_list\": [\n    {\n      \"table\": \"attribution_result_rule\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"}\n      ]\n    },\n    {\n      \"table\": \"attribution_result\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"id\",\"type\": \"Integer\",\"isRequired\": true},\n        {\"id\": 2,\"name\": \"rules\",      \"type\": \"List:attribution_result_rule\", \"isRequired\": true}\n      ]\n    },\n    {\n      \"table\": \"impression\",\n      \"fields\": [\n        {\"id\": 1, \"name\": \"media_provider_id\", \"type\": \"Integer\",\"isRequired\": true},\n        {\"id\": 2,\"name\": \"mp_tpt_category_id\",\"type\": \"Short\",  \"default\": \"-1\"},\n        {\"id\": 3,\"name\": \"truncated_url\",     \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 4,\"name\": \"bid\",         \"type\": \"Boolean\",  \"default\": \"false\"},\n        {\"id\": 5,\"name\": \"bid_type\",          \"type\": \"Byte\",   \"default\": \"-1\"},\n        {\"id\": 6,\"name\": \"attribution_results\",\"type\": \"List:attribution_result\"},\n        {\"id\": 7,\"name\": \"allowed_ad_formats\",\"type\": \"Long\",   \"default\": \"-1\"},\n\t\t{\"id\": 8,\"name\": \"cost\",\"type\": \"Double\",   \"default\": \"0\"}\n\t\t]\n    },\n    {\n      \"table\": \"provider_user_id\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"provider_type\",          \"type\": \"Byte\",   \"isRequired\": true},\n        {\"id\": 2,\"name\": \"provider_id\",            \"type\": \"Integer\",\"isRequired\": true}\n      ]\n    },\n    {\n      \"table\": \"id_map\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"media_provider_ids\",\"type\": \"List:provider_user_id\"}\n      ]\n    },\n    {\n      \"table\": \"analytical_user_profile\",\n      \"fields\": [\n        {\"id\": 1,\"name\": \"user_id\",                 \"type\": \"Long\",   \"isRequired\": true, \"isFrequentlyUsed\": true},\n        {\"id\": 2,\"name\": \"version\",         \"type\": \"Byte\", \"isRequired\": true, \"isDesSortKey\": true},\n        {\"id\": 3,\"name\": \"resolution\",      \"type\": \"Short\",  \"default\": \"-1\"},\n        {\"id\": 4,\"name\": \"os_version\",      \"type\": \"String\", \"default\": \"Unknown\"},\n        {\"id\": 5,\"name\": \"impressions\",            \"type\": \"List:impression\"},\n        {\"id\": 6,\"name\": \"id_maps\",                \"type\": \"id_map\"},\n\t\t{\"id\": 7, \"name\": \"time_list\",     \"type\": \"List:Integer\"}\n      ]\n    }\n  ]\n}";
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate = 
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	@Test(expectedExceptions = FieldConstraintModifiedInSchemaEvolutionException.class)
	public void checkFieldConstraintChangedSchema1() throws AbstractValidationException {
		String json_schema = "{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"attribution_result_rule\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"attribution_result\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"id\",\"type\": \"Integer\",\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"rules\",      \"type\": \"List:attribution_result_rule\", \"isRequired\": true}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"impression\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, \"name\": \"media_provider_id\", \"type\": \"Integer\",\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"mp_tpt_category_id\",\"type\": \"Short\",  \"default\": \"-1\"},\r\n" + 
				"        {\"id\": 3,\"name\": \"truncated_url\",     \"type\": \"String\", \"default\": \"Unknown\"},\r\n" + 
				"        {\"id\": 4,\"name\": \"bid\",         \"type\": \"Boolean\",  \"default\": \"true\"},\r\n" + 
				"        {\"id\": 5,\"name\": \"bid_type\",          \"type\": \"Byte\",   \"default\": \"-1\"},\r\n" + 
				"        {\"id\": 6,\"name\": \"attribution_results\",\"type\": \"List:attribution_result\"},\r\n" + 
				"        {\"id\": 7,\"name\": \"allowed_ad_formats\",\"type\": \"Long\",   \"default\": \"-1\"},\r\n" + 
				"		{\"id\": 8,\"name\": \"cost\",\"type\": \"Double\",   \"default\": \"0\"}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"provider_user_id\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"provider_type\",          \"type\": \"Byte\",   \"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"provider_id\",            \"type\": \"Integer\",\"isRequired\": true}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"id_map\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"media_provider_ids\",\"type\": \"List:provider_user_id\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"analytical_user_profile\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"user_id\",                 \"type\": \"Long\",   \"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"version\",         \"type\": \"Byte\", \"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,\"name\": \"resolution\",      \"type\": \"Short\",  \"isRequired\": true},\r\n" + 
				"        {\"id\": 4,\"name\": \"os_version\",      \"type\": \"String\", \"default\": \"Unknown\"},\r\n" + 
				"        {\"id\": 5,\"name\": \"impressions\",            \"type\": \"List:impression\"},\r\n" + 
				"        {\"id\": 6,\"name\": \"id_maps\",                \"type\": \"id_map\"},\r\n" + 
				"		{\"id\": 7, \"name\": \"time_list\",     \"type\": \"List:Integer\"}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate = 
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	@Test(expectedExceptions = FieldConstraintModifiedInSchemaEvolutionException.class)
	public void checkFieldConstraintChangedSchema2() throws AbstractValidationException {
		String json_schema = "{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"attribution_result_rule\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"attribution_result\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"id\",\"type\": \"Integer\",\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"rules\",      \"type\": \"List:attribution_result_rule\", \"isRequired\": true}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"impression\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, \"name\": \"media_provider_id\", \"type\": \"Integer\",\"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"mp_tpt_category_id\",\"type\": \"Short\",  \"default\": \"-1\"},\r\n" + 
				"        {\"id\": 3,\"name\": \"truncated_url\",     \"type\": \"String\", \"default\": \"Unknown\"},\r\n" + 
				"        {\"id\": 4,\"name\": \"bid\",         \"type\": \"Boolean\",  \"default\": \"true\"},\r\n" + 
				"        {\"id\": 5,\"name\": \"bid_type\",          \"type\": \"Byte\",   \"default\": \"-1\"},\r\n" + 
				"        {\"id\": 6,\"name\": \"attribution_results\",\"type\": \"List:attribution_result\"},\r\n" + 
				"        {\"id\": 7,\"name\": \"allowed_ad_formats\",\"type\": \"Long\",   \"default\": \"-1\"},\r\n" + 
				"		{\"id\": 8,\"name\": \"cost\",\"type\": \"Double\",   \"default\": \"0\"}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"provider_user_id\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"provider_type\",          \"type\": \"Byte\",   \"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"provider_id\",            \"type\": \"Integer\",\"isRequired\": true}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"id_map\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"media_provider_ids\",\"type\": \"List:provider_user_id\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"analytical_user_profile\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"user_id\",                 \"type\": \"Long\",   \"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"version\",         \"type\": \"Byte\", \"isRequired\": true, \"isDesSortKey\": true},\r\n" + 
				"        {\"id\": 3,\"name\": \"resolution\",      \"type\": \"Short\",  \"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,\"name\": \"os_version\",      \"type\": \"String\", \"default\": \"Unknown\"},\r\n" + 
				"        {\"id\": 5,\"name\": \"impressions\",            \"type\": \"List:impression\"},\r\n" + 
				"        {\"id\": 6,\"name\": \"id_maps\",                \"type\": \"id_map\"},\r\n" + 
				"		{\"id\": 7, \"name\": \"time_list\",     \"type\": \"List:Integer\"}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate = 
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
	
	@Test(expectedExceptions = FieldConstraintModifiedInSchemaEvolutionException.class)
	public void checkFieldConstraintChangedSchema3() throws AbstractValidationException {
		String json_schema = "{\r\n" + 
				"  \"schema\": \"simple_schema\",\r\n" + 
				"  \"table_list\": [\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"attribution_result_rule\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"run_num\",    \"type\": \"Byte\",   \"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"value\",      \"type\": \"String\", \"default\": \"Unknown\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"attribution_result\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"id\",\"type\": \"Integer\",\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"rules\",      \"type\": \"List:attribution_result_rule\", \"isRequired\": true}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"impression\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1, \"name\": \"media_provider_id\", \"type\": \"Integer\",\"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"mp_tpt_category_id\",\"type\": \"Short\",  \"default\": \"-1\"},\r\n" + 
				"        {\"id\": 3,\"name\": \"truncated_url\",     \"type\": \"String\", \"default\": \"Unknown\"},\r\n" + 
				"        {\"id\": 4,\"name\": \"bid\",         \"type\": \"Boolean\",  \"default\": \"true\"},\r\n" + 
				"        {\"id\": 5,\"name\": \"bid_type\",          \"type\": \"Byte\",   \"default\": \"-1\"},\r\n" + 
				"        {\"id\": 6,\"name\": \"attribution_results\",\"type\": \"List:attribution_result\"},\r\n" + 
				"        {\"id\": 7,\"name\": \"allowed_ad_formats\",\"type\": \"Long\",   \"default\": \"-1\"},\r\n" + 
				"		{\"id\": 8,\"name\": \"cost\",\"type\": \"Double\",   \"default\": \"0\"}\r\n" + 
				"		]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"provider_user_id\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"provider_type\",          \"type\": \"Byte\",   \"isRequired\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"provider_id\",            \"type\": \"Integer\",\"isRequired\": true}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"id_map\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"media_provider_ids\",\"type\": \"List:provider_user_id\"}\r\n" + 
				"      ]\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"      \"table\": \"analytical_user_profile\",\r\n" + 
				"      \"fields\": [\r\n" + 
				"        {\"id\": 1,\"name\": \"user_id\",                 \"type\": \"Long\",   \"isRequired\": true, \"isFrequentlyUsed\": true},\r\n" + 
				"        {\"id\": 2,\"name\": \"version\",         \"type\": \"Byte\", \"isRequired\": true},\r\n" + 
				"        {\"id\": 3,\"name\": \"resolution\",      \"type\": \"Short\",  \"default\": \"-1\"},\r\n" + 
				"        {\"id\": 4,\"name\": \"os_version\",      \"type\": \"String\", \"default\": \"Unknown\"},\r\n" + 
				"        {\"id\": 5,\"name\": \"impressions\",            \"type\": \"List:impression\"},\r\n" + 
				"        {\"id\": 6,\"name\": \"id_maps\",                \"type\": \"id_map\"},\r\n" + 
				"		{\"id\": 7, \"name\": \"time_list\",     \"type\": \"List:Integer\"}\r\n" + 
				"      ]\r\n" + 
				"    }\r\n" + 
				"  ]\r\n" + 
				"}";
		Schema nextSchema = new JsonSchemaConvertor().apply(json_schema);
		SchemaEvolutionValidation validate = 
				new SchemaEvolutionValidation(currentSchema);
		validate.check(nextSchema);
	}
}
