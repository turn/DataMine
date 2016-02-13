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
package datamine.storage.recordbuffers.example.model;

import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Field;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.PrimitiveType;
import datamine.storage.idl.type.FieldTypeFactory;



/**
 * DO NOT CHANGE! Auto-generated code
 */
public enum MainTableMetadata implements RecordMetadataInterface {

	LONG_REQUIRED_COLUMN((short)1, "long_required_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT64), true, null, false, false, true, false),
	INT_SORTED_COLUMN((short)2, "int_sorted_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32), true, null, true, false, false, false),
	BYTE_COLUMN((short)3, "byte_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.BYTE), false, (byte)-1, false, false, false, false),
	BOOLEAN_COLUMN((short)4, "boolean_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.BOOL), false, (boolean)false, false, false, false, false),
	SHORT_COLUMN((short)5, "short_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT16), false, (short)0, false, false, false, false),
	FLOAT_COLUMN((short)6, "float_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.FLOAT), false, (float)0.0, false, false, false, false),
	DOUBLE_COLUMN((short)7, "double_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.DOUBLE), false, (double)0.001, false, false, false, false),
	STRING_COLUMN((short)8, "string_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.STRING), false, (String)"Unknown", false, false, false, false),
	BINARY_COLUMN((short)9, "binary_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.BINARY), false, null, false, false, false, false),
	NESTED_TABLE_COLUMN((short)10, "nested_table_column", FieldTypeFactory.getListType(FieldTypeFactory.getGroupType(FirstLevelNestedTableMetadata.class)), false, null, false, false, false, false),
	STRUCT_COLUMN((short)11, "struct_column", FieldTypeFactory.getGroupType(StructTableMetadata.class), false, null, false, false, false, false),
	INT_LIST_COLUMN((short)12, "int_list_column", FieldTypeFactory.getListType(FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32)), false, null, false, false, false, false),
	STRING_DERIVED_COLUMN((short)0, "string_derived_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.STRING), false, (String)"Unknown", false, false, false, true),
	INT_DERIVED_COLUMN((short)0, "int_derived_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32), false, (int)0, false, false, false, true),
;

	static final short version = 1;
	static final String name = "main_table";
	private Field field;

	private MainTableMetadata(short id, String name, FieldType type, 
		boolean isRequired, Object defaultValue, boolean isDesSorted, 
		boolean isAscSorted, boolean isFrequentlyUsed, boolean isDerived) {
		field = Field.newBuilder(id, name, type).
				withDefaultValue(defaultValue).
				isRequired(isRequired).
				isDesSorted(isDesSorted).
				isAscSorted(isAscSorted).
				isFrequentlyUsed(isFrequentlyUsed).
				isDerived(isDerived).
				build();
	}

	@Override
	public Field getField() { 
		return field; 
	}

	@Override
	public short getVersion() { 
		return version; 
	}

	@Override
	public String getTableName() { 
		return name; 
	}
}


