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
package datamine.storage.idl.json;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;

import datamine.storage.idl.Schema;
import datamine.storage.idl.json.JsonSchema;
import datamine.storage.idl.json.JsonSchemaConvertor;

public class JsonSchemaConvertorTest {

	public final static String WRONG_SCHEMA_IN_JSON = "{\n  \"schema\": \"aup\",\n  \"table_list\": " +
			"[\n    {\n      \"table\": \"attribution_result_rule\",\n      " +
			"\"fields\": [\n        {\"id\": 1,\"name\": \"run_num\",    " +
			"\"type\": \"Byte\",   \"isRequired\": true},\n        " +
			"{\"id\": 2,\"name\": \"category_id\",\"type\": \"String\", " +
			"\"default\": \"\\\"Unknown\\\"\"},\n        " +
			"{\"id\": 3,\"name\": \"keyword\",    \"type\": \"String\", " +
			"\"default\": \"\\\"Unknown\\\"\"},\n        " +
			"{\"id\": 4,\"name\": \"key\",        " +
			"\"type\": \"String\", \"default\": \"\\\"Unknown\\\"\"},\n        " +
			"{\"id\": 5,\"name\": \"value\",      " +
			"\"type\": \"String\", \"default\": \"\\\"Unknown\\\"\"}\n      " +
			"]\n    },\n    {\n      \"table\": \"attribution_result\",\n      " +
			"\"fields\": [\n        {\"id\": 1,\"name\": \"contract_id\",\"type\": " +
			"\"Integer\",\"isRequired\": true},\n        " +
			"{\"id\": 2,\"name\": \"data_cost\",  \"type\": \"Float\",  \"isRequired\": true},\n        " +
			"{\"id\": 3,\"name\": \"rules\",      \"type\": \"List:unknown_attribution_result_rule\"}\n      ]\n    }\n    ]\n}";
	
	@Test
	public void getSchema() throws IOException {
		String filePath = "src/test/resources/SimpleSchema.json";
		// 1. load the schema into the class
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		JsonSchema jSchema = new Gson().fromJson(br, JsonSchema.class);

		// 2. read the schema from the input JSON
		JsonSchemaConvertor convertor = new JsonSchemaConvertor();
		Schema dmSchema = convertor.apply(
				Files.toString(new File(filePath), Charsets.UTF_8));
		
		// 3. check the consistency
		Assert.assertEquals(dmSchema.getName(), jSchema.getName());
		Assert.assertEquals(dmSchema.getTableList().size(), jSchema.getTableList().size());
	}
	
	@Test (expectedExceptions = IllegalArgumentException.class)
	public void testSchemaValidation() {
		JsonSchemaConvertor convertor = new JsonSchemaConvertor();
		convertor.apply(WRONG_SCHEMA_IN_JSON);
	}
}
