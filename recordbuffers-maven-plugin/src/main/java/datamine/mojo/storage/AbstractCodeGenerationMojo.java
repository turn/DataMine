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

import java.io.File;
import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import datamine.storage.idl.Schema;
import datamine.storage.idl.generator.metadata.MetadataPackageToSchema;
import datamine.storage.idl.json.JsonSchemaConvertor;
import datamine.storage.idl.validate.SchemaEvolutionValidation;
import datamine.storage.idl.validate.SchemaValidation;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;

/**
 * @author yqi
 * @date Mar 3, 2015
 */
abstract public class AbstractCodeGenerationMojo extends AbstractMojo {
	
	/**
     * Location of the JSON file for DataMine schema.
     * @parameter
     * @required
     */
    protected File schemaPath;
    
    /**
     * A string for the package name 
     * @parameter
     * @required
     */
    protected String packageName;
    
    
    /**
     * Location of the files to write in
     * @parameter property="project.build.sourceDirectory"
     * @required
     */
    protected File outputDirectory;
    
    /**
	 * Location of the files to write in
	 * @parameter property="project.build.testSourceDirectory"
	 * @required
	 */
	protected File testOutputDirectory;

    
    protected void validateArguments() throws MojoFailureException {
    	if (packageName == null || schemaPath == null || schemaPath.length() == 0) {
    		
    		StringBuilder sb = new StringBuilder()
    			.append("An invalid package name or schema path occurs! \n")
    			.append("Package Name is : ").append(packageName).append("\n")
    			.append("Schema Path is: ").append(schemaPath);
    		
    		throw new MojoFailureException(sb.toString());
    	}
    	//
    	try {
    		// validate the new schema
    		Schema nextSchema = new JsonSchemaConvertor().apply(
					Files.toString(schemaPath, Charsets.UTF_8));
			new SchemaValidation().check(nextSchema);
			
			// check its compatibility with the old one
    		Schema currentSchema = new MetadataPackageToSchema().apply(packageName);
    		if (currentSchema != null) {
    			new SchemaEvolutionValidation(currentSchema).check(nextSchema);
    		} 
    	} catch (AbstractValidationException e) {
    		throw new MojoFailureException(e.getMessage());
    	} catch (IOException e) {
    		throw new MojoFailureException("Cannot read the schema from " + schemaPath, e);
		}
    }
}
