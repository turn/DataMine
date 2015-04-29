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
package datamine.storage.recordbuffers.example.model;

import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Field;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.FieldTypeFactory;
import datamine.storage.idl.type.PrimitiveType;



/**
 * DO NOT CHANGE! Auto-generated code
 */
public enum AnalyticalUserProfileMetadata implements RecordMetadataInterface {

	USER_ID((short)1, "user_id", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT64), true, null, false, false, true),
	VERSION((short)2, "version", FieldTypeFactory.getPrimitiveType(PrimitiveType.BYTE), true, null, true, false, false),
	RESOLUTION((short)3, "resolution", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT16), false, (short)-1),
	OS_VERSION((short)4, "os_version", FieldTypeFactory.getPrimitiveType(PrimitiveType.STRING), false, (String)"Unknown"),
	IMPRESSIONS((short)5, "impressions", FieldTypeFactory.getListType(FieldTypeFactory.getGroupType(ImpressionMetadata.class)), false, null),
	ID_MAPS((short)6, "id_maps", FieldTypeFactory.getGroupType(IdMapMetadata.class), false, null),
	TIME_LIST((short)7, "time_list", FieldTypeFactory.getListType(FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32)), false, null),
;

	static final short version = 1;
	static final String name = "analytical_user_profile";
	private Field field;

	private AnalyticalUserProfileMetadata(short id, String name, FieldType type, boolean isRequired, Object defaultValue) {
		this(id,name, type, isRequired, defaultValue, false, false, false);
	}

	private AnalyticalUserProfileMetadata(short id, String name, FieldType type, boolean isRequired, 
		Object defaultValue, boolean isSorted, boolean isAscSorted, boolean hasRef) {
		field = new Field(id,name, type, defaultValue, Field.getContraintEnumSet(
				isRequired, isSorted, isAscSorted, hasRef));
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

