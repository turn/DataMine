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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datamine.storage.idl.Field;
import datamine.storage.idl.Schema;
import datamine.storage.idl.Table;
import datamine.storage.idl.Field.Constraint;
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.idl.validate.exceptions.AbstractValidationException;
import datamine.storage.idl.validate.exceptions.FieldConstraintModifiedInSchemaEvolutionException;
import datamine.storage.idl.validate.exceptions.FieldDefaultValueModifiedInSchemaEvolutionException;
import datamine.storage.idl.validate.exceptions.FieldDeletionInSchemaEvolutionException;
import datamine.storage.idl.validate.exceptions.FieldTypeModifiedInSchemaEvolutionException;
import datamine.storage.idl.validate.exceptions.SmallerTableVersionInSchemaEvolutionException;

/**
 * The class validates the user change on the existing schema. 
 * 
 * <p>
 * The following restrictions must be enforced:
 * 
 * <i> No new REQUIRED field can be added to any existing table; adding OPTIONAL field is OK.
 * <i> An OPTIONAL field cannot be changed to REQUIRED.
 * <i> The type of existing field cannot be changed.
 * <i> No existing field can be removed.
 * <i> The type of existing fields should not be changed.
 * <i> The default value of existing fields should not be changed.
 * <i> The way of sorting should not be changed if a field is the sort-key
 * <i> At most one sort key is allowed in the table
 * <i> The field can not be changed from non-sort key into sort key, or vise versa. 
 * 
 * </p>
 * 
 * @author yqi
 */
public class SchemaEvolutionValidation implements ValidateInterface<Schema> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SchemaEvolutionValidation.class);
			
	private final Schema currentSchema;
	
	public SchemaEvolutionValidation(Schema curSchema) {
		this.currentSchema = curSchema;
	}
	
	@Override
	public void check(Schema nextSchema) throws AbstractValidationException {
		
		// 1. make sure the input is valid
		new SchemaValidation().check(nextSchema);
	
		// 2. find the corresponding tables to validate the change if necessary
		List<Table> tablesInCurrentSchema = currentSchema.getTableList();
		List<Table> tablesInNextSchema = nextSchema.getTableList();
		
		for (Table cur : tablesInCurrentSchema) {
			boolean found = false;
			for (Table next : tablesInNextSchema) {
				if (cur.getName().equals(next.getName())) {
					checkTable(cur, next);
					found = true;
					break;
				}
			}
			if (!found) {
				LOG.warn("A table is removed - " + cur.getName());
			}
		}
		
	}
	
	private void checkTable(Table currentTable, Table nextTable) throws AbstractValidationException {
		//1. check the version #
		if (nextTable.getVersion() < currentTable.getVersion()) {
			throw new SmallerTableVersionInSchemaEvolutionException(
					currentTable.getVersion(), nextTable.getVersion());
		}
		//2. check fields in the table
		List<Field> fieldsInCurrentTable = currentTable.getFields();
		List<Field> fieldsInNextTable = nextTable.getFields();
		
		//2.1 ensure all fields in the current table do exist still
		if (fieldsInCurrentTable.size() > fieldsInNextTable.size()) {
			throw new FieldDeletionInSchemaEvolutionException("Some");
		}
		
		for (Field cur : fieldsInCurrentTable) {
			boolean found = false;
			for (Field next : fieldsInNextTable) {
				if (cur.getId() == next.getId()) {
					checkField(cur, next);				
					found = true;
					break;
				}
			}
			if (!found) {
				throw new FieldDeletionInSchemaEvolutionException(cur.getName());
			}
		}
	}
	
	private void checkField(Field current, Field next) throws AbstractValidationException {
		// check the name
		if (!current.getName().equals(next.getName())) {
			// it may be OK as the data don't store names
			LOG.warn("Field name changes from " 
				+ current.getName() + " into " + next.getName());
		}
		// check the type
		if (!current.getType().equals(next.getType())) {
			throw new FieldTypeModifiedInSchemaEvolutionException(current.getName());
		}
		// check the constraints
		EnumSet<Constraint> curConstraints = current.getConstraints();
		EnumSet<Constraint> nextConstraints = next.getConstraints();
		
		if (nextConstraints.contains(Constraint.REQUIRED) && 
			curConstraints.contains(Constraint.OPTIONAL)) {
			throw new FieldConstraintModifiedInSchemaEvolutionException(
					"Cannot change an optional field into the required : " + current.getName()); 
		}
		
		if ((nextConstraints.contains(Constraint.ASC_SORTED) && 
			!curConstraints.contains(Constraint.ASC_SORTED)) ||
			(nextConstraints.contains(Constraint.DES_SORTED) && 
			!curConstraints.contains(Constraint.DES_SORTED))) {
			throw new FieldConstraintModifiedInSchemaEvolutionException(
					"Cannot make a sort key from any existing field : " + current.getName()); 
		}
		
		if ((curConstraints.contains(Constraint.DES_SORTED) && 
			!nextConstraints.contains(Constraint.DES_SORTED)) ||
			(curConstraints.contains(Constraint.ASC_SORTED) && 
			!nextConstraints.contains(Constraint.ASC_SORTED))) {
			throw new FieldConstraintModifiedInSchemaEvolutionException(
					"Cannot make eliminate a sort key field from : " + current.getName()); 
		}

		// check the default value
		if (!current.isRequired() && current.getType() instanceof PrimitiveFieldType &&
			!current.getDefaultValue().equals(next.getDefaultValue())) {
			throw new FieldDefaultValueModifiedInSchemaEvolutionException(current.getName());
		}
			
		
	}
	
}
