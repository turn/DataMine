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
package datamine.mojo.storage.recordbuffers;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import datamine.mojo.storage.AbstractCodeGenerationMojo;
import datamine.storage.idl.Schema;
import datamine.storage.idl.json.JsonSchemaConvertor;
import datamine.storage.recordbuffers.idl.generator.RecordMetaWrapperGenerator;

/**
 * Goal which creates a set of Java classes as the implementation of 
 * the table access interfaces. 
 *
 * @goal record_buffer_tables
 * 
 * @phase generate-sources
 */
public class RecordBuffersTableGenerationMojo extends AbstractCodeGenerationMojo
{
    /**
     * A string for the interface package name
     *   
     * @parameter
     * @required
     */
    protected String interfacePackageName;
    
    /**
     * A string for the table schema package name
     *   
     * @parameter
     * @required
     */
    protected String metadataPackageName;
    
	public void execute()
			throws MojoExecutionException, MojoFailureException
	{
		validateArguments();

		try {
			//0. generate the java source codes
			Schema schema = new JsonSchemaConvertor().apply(
					Files.toString(schemaPath, Charsets.UTF_8));

			//1. generate wrapper codes
			RecordMetaWrapperGenerator gen = new RecordMetaWrapperGenerator(
					outputDirectory.getAbsolutePath(), 
					packageName,
					metadataPackageName,
					interfacePackageName);
			gen.apply(schema);

		} catch (IOException e) {
			throw new MojoExecutionException( "Error generating Java classes for table access (by Record Buffers)!", e );
		}
	}
}
