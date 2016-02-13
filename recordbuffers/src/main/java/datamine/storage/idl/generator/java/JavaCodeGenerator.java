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
package datamine.storage.idl.generator.java;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import datamine.storage.idl.generator.CodeGenerator;
import datamine.storage.idl.generator.CodeTemplate;

/**
 * A generator for a Java interface/class, creates the header and defines
 * a template of package, import and body. 
 * 
 * @author yqi
 * @date Feb 27, 2015
 */
public class JavaCodeGenerator implements CodeGenerator {
	
	private final String[] codeTemplate = {
			"/**", 
			" * Copyright (C) 2015 Turn Inc.", 
			" *",
			" * Licensed under the Apache License, Version 2.0 (the \"License\");", 
			" * you may not use this file except in compliance with the License.", 
			" * You may obtain a copy of the License at",
			" *",
			" *    	http://www.apache.org/licenses/LICENSE-2.0", 
			" *", 
			" * Unless required by applicable law or agreed to in writing, software", 
			" * distributed under the License is distributed on an \"AS IS\" BASIS,", 
			" * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.", 
			" * See the License for the specific language governing permissions and", 
			" * limitations under the License.", 
			" */",
			"package {package};",
			"",
			"{import}",
			"",
			"/**",
			" * DO NOT CHANGE! Auto-generated code",
			" */",
			"{body}",
	};
	
	private final CodeTemplate code = new CodeTemplate(codeTemplate);
	private final String packageName;
	private final String className;
	private final String sourceDir; 
	
	public JavaCodeGenerator(String sourceDir, String packageName, 
			String className, CodeTemplate importCode, CodeTemplate body) {
		code.fillFields("body", body);
		code.fillFields("import", importCode);
		code.fillFields("package", packageName);
		this.packageName = packageName;
		this.className = className;
		this.sourceDir = sourceDir;
	}

	@Override
	public void generate() {		
		FileWriter fWriter = null;
		try {
			File file = new File(sourceDir + File.separatorChar +
					packageName.replace('.', File.separatorChar) + 
					File.separatorChar + className + ".java");
			file.getParentFile().mkdirs();
			fWriter = new FileWriter(file);
			fWriter.write(code.getCode());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fWriter != null) {
				try {
					fWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
