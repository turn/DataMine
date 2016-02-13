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
package datamine.mojo.storage;

import java.io.IOException;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import datamine.storage.idl.Schema;
import datamine.storage.idl.generator.java.InterfaceConvertorGenerator;
import datamine.storage.idl.json.JsonSchemaConvertor;

/**
 * Goal which creates a set of Java classes to copy a table record as an interface
 * into another table record as the same interface. 
 *
 * @goal table_interface_convertors
 * 
 * @phase generate-sources
 */
public class TableInterfaceConvertorGenerationMojo
    extends AbstractCodeGenerationMojo
{
	/**
     * A string for the interface package name
     *   
     * @parameter
     * @required
     */
    protected String interfacePackageName;
    
	public void execute()
        throws MojoExecutionException, MojoFailureException
    {
    	validateArguments();
    	
    	InterfaceConvertorGenerator generator = new InterfaceConvertorGenerator(
				outputDirectory.getAbsolutePath(), 
				packageName, interfacePackageName);

		try {
			// generate the java source codes
			Schema schema = new JsonSchemaConvertor().apply(
					Files.toString(schemaPath, Charsets.UTF_8));
			schema.accept(generator);
			generator.generate();
		} catch (IOException e) {
			throw new MojoExecutionException( "Error generating Java interfaces for table conversion!", e );
		}
    }
}
