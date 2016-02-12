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
package datamine.storage.idl.validate;

import java.util.Set;

import com.google.common.collect.Sets;

import datamine.storage.idl.Schema;
import datamine.storage.idl.Table;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.idl.validate.exceptions.NameDuplicationException;

/**
 * Validate the syntax and semantics of the input schema. 
 * 
 * <p>
 * Note that it does not use the VISITOR pattern as it needs to collect 
 * the information at the schema level. 
 * </p>
 * 
 * @author yqi
 * @date Apr 9, 2015
 */
public class SchemaValidation implements ValidateInterface<Schema> {

	private static final TableValidation tableValidator = 
			new TableValidation();
	
	@Override
	public void check(Schema input) throws AbstractValidationException {
		
		// validate the name
		new NamingConvensionValidation().check(input.getName());
		
		// validate all tables
		Set<String> tableNames = Sets.newHashSet();
		for (Table cur : input.getTableList()) {
			if (tableNames.contains(cur.getName())) {
				throw new NameDuplicationException(cur.getName());
			} else {
				tableNames.add(cur.getName());
			}
			tableValidator.check(cur);;
		}
	}
	
}
