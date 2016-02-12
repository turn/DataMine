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
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.idl.type.PrimitiveType;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.idl.validate.exceptions.IllegalDerivedFieldException;
import datamine.storage.idl.validate.exceptions.IllegalFieldDefaultValueException;
import datamine.storage.idl.validate.exceptions.IllegalFieldIdentityException;
import datamine.storage.idl.validate.exceptions.IllegalFieldRestrictionException;
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
		int curId = input.getId();
		String fieldName = input.getName();
		
		// The ID of derived field must be equal to Field.DERIVED_FIELD_ID 
		if (curId != Field.DERIVED_FIELD_ID && input.isDerived()) {
			throw new IllegalFieldIdentityException(
					"Derived field (\"" + fieldName + "\") must have a unique ID to be : " + Field.DERIVED_FIELD_ID);
		} else if (curId == Field.DERIVED_FIELD_ID && !input.isDerived()) {
			throw new IllegalFieldIdentityException(fieldName + " - " +
					"Non-derived field (\""+ fieldName +"\") must not have a ID to be : " + Field.DERIVED_FIELD_ID);
		}
		
		// derived field should be always primitive
		FieldType type = input.getType();
		if (input.isDerived()) {
			if (!(type instanceof PrimitiveFieldType)) {
				throw new IllegalDerivedFieldException("Derived field (" + 
						fieldName + ") must be primitive!");
			}
			
			if (input.isSortKey()) {
				throw new IllegalDerivedFieldException("Derived field (" + 
						fieldName + ") cannot be used as a sort key!");
			}
			
			if (input.isRequired()) {
				throw new IllegalDerivedFieldException("Derived field (" + 
						fieldName + ") must be optional!");
			}
			
			if (input.isFrequentlyUsed()) {
				throw new IllegalDerivedFieldException("Derived field (" + 
						fieldName + ") cannot be indicated as a frequently used one!");
			}
		}
		
		
		// validate the name
		new NamingConvensionValidation().check(fieldName);
				
		// validate its type and default value
		Object defaultVal = input.getDefaultValue();
		
		if (type instanceof PrimitiveFieldType && 
			((PrimitiveFieldType) type).getPrimitiveType() != PrimitiveType.BINARY && 
			!input.isRequired()) {
			
			if (!FieldValueOperatorFactory.getOperator(type).isValid(defaultVal)) {
				throw new IllegalFieldDefaultValueException(
						fieldName, defaultVal + " is not valid!");
			}
			
		} else {
			if (defaultVal != null) {
				throw new IllegalFieldDefaultValueException(
						fieldName, "No requirement for the default value!");
			}
		}
		
		// validate its constraints
		EnumSet<Constraint> constraints = input.getConstraints();
		if (constraints.contains(Constraint.ASC_SORTED) || constraints.contains(Constraint.DES_SORTED)) {
			if (!constraints.contains(Constraint.REQUIRED)) {
				throw new OptionalSortKeyException(fieldName);
			}
		}
		
		// validate the constraints for hints
		if (constraints.contains(Constraint.LARGE_LIST)) {
			if (!(type instanceof CollectionFieldType)) {
				String msg = Constraint.LARGE_LIST + " cannot be applied to a non-collection type field";
				throw new IllegalFieldRestrictionException(msg);
			}
		}

	}

}
