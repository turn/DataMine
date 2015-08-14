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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import datamine.storage.idl.Schema;
import datamine.storage.idl.generator.TableTestDataGenerator;
import datamine.storage.idl.generator.java.InterfaceConvertorGenerator;
import datamine.storage.idl.generator.java.InterfaceGenerator;
import datamine.storage.idl.generator.java.InterfaceContentPrinterGenerator;
import datamine.storage.idl.generator.metadata.MetadataFileGenerator;
import datamine.storage.idl.json.JsonSchemaConvertor;
import datamine.storage.idl.validate.SchemaValidation;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.recordbuffers.idl.generator.RecordMetaWrapperGenerator;

/**
 * This class is to generate all data for unit testing.
 * 
 * @author yqi
 */
public class GenerateTestData {

	static final Logger LOG = LoggerFactory.getLogger(GenerateTestData.class);
	
	private final String schemaFile;
	private final String genSrcFolder;
	private final String genClassPackageName;
		
	private Schema schema = null;
	
	public GenerateTestData(String file, String folder, String packageName) { 
		this.schemaFile = file;
		this.genClassPackageName = packageName;
		this.genSrcFolder = folder;		
	}
	
	public void run() {
		File schemaPath = new File(this.schemaFile);
		try {
			schema = new JsonSchemaConvertor().apply(
					Files.toString(schemaPath, Charsets.UTF_8));
			
			// validate the schema
			new SchemaValidation().check(schema);
			
			// generate the codes
			generateTableAccessInterfaces();
			generateTableConversionImps();
			generateTableMetadataEnums();
			generateTableAccessImps();
			generateTableAccessTestData();
			generateTableContentPrinters();
			
		} catch (IOException e) {
			LOG.error("Cannot generate the code for the schema at " + this.schemaFile, e);
		} catch (AbstractValidationException e) {
			LOG.error("Schema validation failed for " + this.schemaFile, e);
		}
	}
	
	private void generateTableAccessInterfaces() {
		InterfaceGenerator generator = new InterfaceGenerator(
				genSrcFolder, genClassPackageName + ".interfaces");

		// generate the java source codes
		schema.accept(generator);
		generator.generate();
	}
	
	private void generateTableConversionImps() {
		InterfaceConvertorGenerator generator0 = new InterfaceConvertorGenerator(
				genSrcFolder,
				genClassPackageName + ".convertors",
				genClassPackageName + ".interfaces");

		// generate the java source codes
		schema.accept(generator0);
		generator0.generate();
	}
	
	private void generateTableContentPrinters() {
		InterfaceContentPrinterGenerator generator = new InterfaceContentPrinterGenerator(
				genSrcFolder,
				genClassPackageName + ".printers",
				genClassPackageName + ".interfaces");

		// generate the java source codes
		schema.accept(generator);
		generator.generate();
	}
	
	private void generateTableMetadataEnums() {
		MetadataFileGenerator generator1 = new MetadataFileGenerator(
				this.genSrcFolder, genClassPackageName + ".model");
		generator1.apply(schema);
	}
	
	private void generateTableAccessImps() {
		RecordMetaWrapperGenerator gen = new RecordMetaWrapperGenerator(
				genSrcFolder,
				genClassPackageName + ".wrapper",
				genClassPackageName + ".model",
				genClassPackageName + ".interfaces");
		gen.apply(schema);
	}
	
	private void generateTableAccessTestData() {
		TableTestDataGenerator gen2 = new TableTestDataGenerator(
				genSrcFolder,
				genClassPackageName + ".data",
				genClassPackageName + ".model",
				genClassPackageName + ".interfaces");
		gen2.apply(schema);
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws AbstractValidationException 
	 */
	public static void main(String[] args) throws IOException, AbstractValidationException {

		GenerateTestData gtd = new GenerateTestData(
				"src/test/resources/RBSchema.json", 
				"src/test/java/", 
				"datamine.storage.recordbuffers.example");
		
		gtd.run();
	}

}
