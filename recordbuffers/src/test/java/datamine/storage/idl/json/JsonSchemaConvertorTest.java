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

	public final static String WRONG_SCHEMA_IN_JSON = 
			"{\r\n" + 
			"  \"schema\": \"simple_schema\",\r\n" + 
			"  \"table_list\": [\r\n" + 
			"    {\r\n" + 
			"      \"table\": \"table\",\r\n" + 
			"      \"fields\": [\r\n" + 
			"        {\"id\": 1,\"name\": \"byte_required_column\",	\"type\": \"Byte\",		\"isRequired\": true},\r\n" + 
			"        {\"id\": 2,\"name\": \"boolean_list_column\",  \"type\": \"List:nested_table\"}\r\n" + 
			"      ]\r\n" + 
			"    }\r\n" + 
			"  ]\r\n" + 
			"}";
	
	@Test
	public void getSchema() throws IOException {
		String filePath = "src/test/resources/RBSchema.json";
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
	
	/**
	 * A undefined table is referred, i.e., nested_table
	 */
	@Test (expectedExceptions = IllegalArgumentException.class)
	public void testSchemaValidation() {
		JsonSchemaConvertor convertor = new JsonSchemaConvertor();
		convertor.apply(WRONG_SCHEMA_IN_JSON);
	}
}
