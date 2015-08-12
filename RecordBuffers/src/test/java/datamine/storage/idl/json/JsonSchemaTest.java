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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.gson.Gson;

import datamine.storage.idl.json.JsonSchema;
import datamine.storage.idl.json.JsonTable;

public class JsonSchemaTest {
	
	@Test
	public void testLoading() throws FileNotFoundException {
		BufferedReader br = new BufferedReader(new FileReader("src/test/resources/RBSchema.json"));
		JsonSchema schema = new Gson().fromJson(br, JsonSchema.class);
		
		List<JsonTable> tables = schema.getTableList();
		Assert.assertEquals(4, tables.size());
	}
}
