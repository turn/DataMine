package datamine.storage.recordbuffers.example.model;

import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Field;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.PrimitiveType;
import datamine.storage.idl.type.FieldTypeFactory;



/**
 * DO Not CHANGE! Auto-generated code
 */
public enum StructTableMetadata implements RecordMetadataInterface {

	NESTED_TABLE_COLUMN((short)1, "nested_table_column", FieldTypeFactory.getListType(FieldTypeFactory.getGroupType(SecondLevelNestedTableMetadata.class)), false, null, false, false, false, false),
	INT_SORTED_COLUMN((short)2, "int_sorted_column", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32), true, null, false, false, false, false),
;

	static final short version = 1;
	static final String name = "struct_table";
	private Field field;

	private StructTableMetadata(short id, String name, FieldType type, 
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


