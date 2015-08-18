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
package datamine.storage.idl;

import java.io.*;
import java.util.EnumSet;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import datamine.storage.idl.json.JsonSchema;
import datamine.storage.idl.json.JsonSchemaConvertor;
import datamine.storage.idl.type.FieldType;
import org.testng.Assert;
import org.testng.annotations.Test;

import datamine.storage.idl.Field;

public class FieldTest {
	
	@Test
	public void getContraintEnumSet() {
		
		boolean isRequired = false;
		boolean isDesSorted = false;
		boolean isAscSorted = false;
		boolean isFrequentlyUsed = false;
		boolean isDerived = false;
		boolean hasLargeList = false;
		
		EnumSet<Field.Constraint> constraints = Field.getContraintEnumSet(
				isRequired, isDesSorted, isAscSorted, isFrequentlyUsed, 
				isDerived, hasLargeList);
		Assert.assertEquals(constraints, EnumSet.of(Field.Constraint.OPTIONAL));
		
		isRequired = true;
		isDesSorted = false;
		isAscSorted = false;
		
		constraints = Field.getContraintEnumSet(
				isRequired, isDesSorted, isAscSorted, isFrequentlyUsed, 
				isDerived, hasLargeList);
		Assert.assertEquals(constraints, EnumSet.of(Field.Constraint.REQUIRED));
		
		isRequired = true;
		isDesSorted = true;
		isAscSorted = false;
		
		constraints = Field.getContraintEnumSet(
				isRequired, isDesSorted, isAscSorted, isFrequentlyUsed, 
				isDerived, hasLargeList);
		Assert.assertEquals(constraints, EnumSet.of(Field.Constraint.REQUIRED, 
				Field.Constraint.DES_SORTED));
	}
	
	@Test (expectedExceptions=java.lang.IllegalArgumentException.class)
	void checkDerived() {
		boolean isDesSorted = true;
		boolean isAscSorted = true;
		Field.getContraintEnumSet(false, isDesSorted, isAscSorted, false, 
				false, false);
	}

	@Test
	public void testReadWriteSchema() throws IOException {
		String filePath = "src/test/resources/RBSchema.json";
		JsonSchemaConvertor convertor = new JsonSchemaConvertor();
		Schema dmSchema = convertor.apply(
				Files.toString(new File(filePath), Charsets.UTF_8));

		Gson gson = new GsonBuilder().registerTypeAdapter(FieldType.class, new Field.FieldDeserializer()).create();
		Schema rqSchema = gson.fromJson(dmSchema.toString(), Schema.class);
		Assert.assertEquals(dmSchema.getTableList().size(), rqSchema.getTableList().size());
	}
}
