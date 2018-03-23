package datamine.storage.recordbuffers.example.model;

import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Field;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.PrimitiveType;
import datamine.storage.idl.type.FieldTypeFactory;



/**
 * DO Not CHANGE! Auto-generated code
 */
public enum FirstLevelNestedTableMetadata implements RecordMetadataInterface {

	EVENT_TIME((short)1, "event_time", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT64), true, null, false, true, false, false),
	INT_REQUIRED_COLUMN((short)2, "int_required_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32), true, null, false, false, false, false),
	NESTED_TABLE_COLUMN((short)3, "nested_table_column", FieldTypeFactory.getListType(FieldTypeFactory.getGroupType(SecondLevelNestedTableMetadata.class)), false, null, false, false, false, false),
	STRING_DERIVED_COLUMN((short)0, "string_derived_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.STRING), false, (String)"Unknown", false, false, false, true),
;

	static final short version = 1;
	static final String name = "first_level_nested_table";
	private Field field;

	private FirstLevelNestedTableMetadata(short id, String name, FieldType type, 
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


