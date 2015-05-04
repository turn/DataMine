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

import java.util.EnumSet;

import datamine.storage.idl.Field;
import datamine.storage.idl.Field.Constraint;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.idl.validate.exceptions.IllegalFieldDefaultValueException;
import datamine.storage.idl.validate.exceptions.OptionalSortKeyException;
import datamine.storage.recordbuffers.idl.value.FieldValueOperatorFactory;

/**
 * Validate the field structure
 * 
 * @author yqi
 */
class FieldValidation implements ValidateInterface<Field> {

	@Override
	public void check(Field input) throws AbstractValidationException {
		
		// the ID is validated in its holder (i.e., table)
		
		// validate the name
		new NamingConvensionValidation().check(input.getName());
				
		// validate its type and default value
		FieldType type = input.getType();
		Object defaultVal = input.getDefaultValue();
		
		if (type instanceof PrimitiveFieldType && !input.isRequired()) {
			if (!FieldValueOperatorFactory.getOperator(type).isValid(defaultVal)) {
				throw new IllegalFieldDefaultValueException(
						input.getName(), defaultVal + " is not valid!");
			}
		} else {
			if (defaultVal != null) {
				throw new IllegalFieldDefaultValueException(
						input.getName(), "No requirement for the default value!");
			}
		}
		
		// validate its constraints
		EnumSet<Constraint> constraints = input.getConstraints();
		if (constraints.contains(Constraint.ASC_SORTED) || constraints.contains(Constraint.DES_SORTED)) {
			if (!constraints.contains(Constraint.REQUIRED)) {
				throw new OptionalSortKeyException(input.getName());
			}
		}
	}

}
