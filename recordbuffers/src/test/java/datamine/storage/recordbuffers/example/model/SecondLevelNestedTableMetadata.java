package datamine.storage.recordbuffers.example.model;

import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Field;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.PrimitiveType;
import datamine.storage.idl.type.FieldTypeFactory;



/**
 * DO Not CHANGE! Auto-generated code
 */
public enum SecondLevelNestedTableMetadata implements RecordMetadataInterface {

	BYTE_REQUIRED_COLUMN((short)1, "byte_required_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.BYTE), true, null, false, false, false, false),
	BOOLEAN_LIST_COLUMN((short)2, "boolean_list_column", FieldTypeFactory.getListType(FieldTypeFactory.getPrimitiveType(PrimitiveType.BOOL)), false, null, false, false, false, false),
;

	static final short version = 1;
	static final String name = "second_level_nested_table";
	private Field field;

	private SecondLevelNestedTableMetadata(short id, String name, FieldType type, 
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


