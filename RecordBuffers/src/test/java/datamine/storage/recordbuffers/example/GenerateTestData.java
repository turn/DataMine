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
package datamine.storage.recordbuffers.example;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import datamine.storage.idl.Schema;
import datamine.storage.idl.generator.TableTestDataGenerator;
import datamine.storage.idl.generator.java.InterfaceConvertorGenerator;
import datamine.storage.idl.generator.java.InterfaceGenerator;
import datamine.storage.idl.generator.metadata.MetadataFileGenerator;
import datamine.storage.idl.json.JsonSchemaConvertor;
import datamine.storage.idl.validate.SchemaValidation;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.recordbuffers.idl.generator.RecordMetaWrapperGenerator;

/**
 * This class is to generate all data for unit testing.
 * 
 * @author yqi
 * @date Mar 18, 2015
 */
public class GenerateTestData {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws AbstractValidationException 
	 */
	public static void main(String[] args) throws IOException, AbstractValidationException {

		File schemaPath = new File("src/test/resources/SimpleSchema.json");
		Schema schema = new JsonSchemaConvertor().apply(
				Files.toString(schemaPath, Charsets.UTF_8));
		
		// verify
		new SchemaValidation().check(schema); 
		
		InterfaceGenerator generator = new InterfaceGenerator(
				"src/test/java/", 
				"datamine.storage.recordbuffers.example.interfaces");

		// generate the java source codes
		schema.accept(generator);
		generator.generate();

		InterfaceConvertorGenerator generator0 = new InterfaceConvertorGenerator(
				"src/test/java/", 
				"datamine.storage.recordbuffers.example.convertors",
				"datamine.storage.recordbuffers.example.interfaces");

		// generate the java source codes
		schema.accept(generator0);
		generator0.generate();
		
		//1. generate model codes
		MetadataFileGenerator generator1 = new MetadataFileGenerator(
				"src/test/java/", 
				"datamine.storage.recordbuffers.example.model");
		generator1.apply(schema);

		//2. generate wrapper codes
		RecordMetaWrapperGenerator gen = new RecordMetaWrapperGenerator(
				"src/test/java/", 
				"datamine.storage.recordbuffers.example.wrapper",
				"datamine.storage.recordbuffers.example.model",
				"datamine.storage.recordbuffers.example.interfaces");
		gen.apply(schema);
		
		//3. generate the data for unit testing
		TableTestDataGenerator gen2 = new TableTestDataGenerator(
				"src/test/java/", 
				"datamine.storage.recordbuffers.example.data",
				"datamine.storage.recordbuffers.example.model",
				"datamine.storage.recordbuffers.example.interfaces");
		gen2.apply(schema);

	}

}
