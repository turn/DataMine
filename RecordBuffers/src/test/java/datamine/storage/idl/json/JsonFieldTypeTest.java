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

import org.testng.Assert;
import org.testng.annotations.Test;

import datamine.storage.idl.json.JsonFieldType;

public class JsonFieldTypeTest {

	@Test
	public void getElementTypeNameInList() {
		String type = "List:attribution_result_rule";
		Assert.assertEquals(JsonFieldType.getElementTypeInList(type),
				JsonFieldType.Struct);
	}
	
	@Test
	public void getBinaryDefault() {
		String value = "[]";
		byte[] result = (byte[]) JsonFieldType.Binary.parseValue(value);
		Assert.assertEquals(result.length, 0);
		
		value = "[2, 3]";
		result = (byte[]) JsonFieldType.Binary.parseValue(value);
		Assert.assertEquals(result.length, 2);
	}
	
	@Test (expectedExceptions=IllegalAccessError.class)
	public void parseString2Struct() {
		String value = "qw2asfq245";
		JsonFieldType.Struct.parseValue(value);
	}
	
	@Test (expectedExceptions=IllegalAccessError.class)
	public void parseString2List() {
		String value = "qw2asfq245";
		JsonFieldType.List.parseValue(value);
	}
}
