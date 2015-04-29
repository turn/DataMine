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

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import datamine.storage.idl.Field;
import datamine.storage.idl.Table;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.idl.validate.exceptions.IdentityDuplicationException;
import datamine.storage.idl.validate.exceptions.IllegalFieldIdentityException;
import datamine.storage.idl.validate.exceptions.IllegalTableVersionException;
import datamine.storage.idl.validate.exceptions.MultipleSortKeysException;
import datamine.storage.idl.validate.exceptions.NameDuplicationException;

/**
 * Validate the table structure to ensure:
 * 
 * <p>
 * <i> It should have the correct name
 * <i> Its version number must be positive
 * <i> It doesn't have multiple fields with the same name
 * <i> Its fields should have positive and continually increasing IDs
 * <i> Every field should be valid
 * </p>
 * 
 * @author yqi
 */
class TableValidation implements ValidateInterface<Table> {

	private static final FieldValidation fieldValidator = 
			new FieldValidation();
	
	@Override
	public void check(Table input) throws AbstractValidationException {
		
		// validate the name
		new NamingConvensionValidation().check(input.getName());
		
		// validate the version information
		if (input.getVersion() < 1) {
			throw new IllegalTableVersionException(input.getVersion());
		}
				
		Set<String> fieldNames = Sets.newHashSet();
		Set<Integer> fieldIdSet = Sets.newHashSet();
		List<Integer> fieldIds = Lists.newArrayList();
		Set<Field> sortedKeys = Sets.newHashSet();
		for (Field cur : input.getFields()) {
			// check if the name is unique
			if (fieldNames.contains(cur.getName())) {
				throw new NameDuplicationException(cur.getName());
			} else {
				fieldNames.add(cur.getName());
			}
			
			// check if the ID is unique
			if (fieldIdSet.contains(cur.getId())) {
				throw new IdentityDuplicationException(cur.getId());
			} else {
				fieldIdSet.add(cur.getId());	
				fieldIds.add(cur.getId());
			}
			
			if (cur.isDesSortKey()) {
				sortedKeys.add(cur);
			}
			
			fieldValidator.check(cur);
		}
		// validate the field IDs
		Collections.sort(fieldIds);
		int len = fieldIds.size();
		if (fieldIds.get(0) != 1) {
			throw new IllegalFieldIdentityException(
					"Field Id must start at 1");
		} 
		
		if (fieldIds.get(len-1) != len) {
			throw new IllegalFieldIdentityException(
					"Field Id must increase continuously");
		}
		
		// at most one sort key is allowed
		if (sortedKeys.size() > 1) {
			throw new MultipleSortKeysException(input.getName());
		}
	}

}
