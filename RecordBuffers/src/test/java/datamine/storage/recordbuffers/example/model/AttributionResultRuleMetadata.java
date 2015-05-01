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
import datamine.storage.idl.type.PrimitiveType;
import datamine.storage.idl.type.FieldTypeFactory;



/**
 * DO NOT CHANGE! Auto-generated code
 */
public enum AttributionResultRuleMetadata implements RecordMetadataInterface {

	RUN_NUM((short)1, "run_num", FieldTypeFactory.getPrimitiveType(PrimitiveType.BYTE), true, null),
	VALUE((short)2, "value", FieldTypeFactory.getPrimitiveType(PrimitiveType.STRING), false, (String)"Unknown"),
;

	static final short version = 1;
	static final String name = "attribution_result_rule";
	private Field field;

	private AttributionResultRuleMetadata(short id, String name, FieldType type, boolean isRequired, Object defaultValue) {
		this(id,name, type, isRequired, defaultValue, false, false, false);
	}

	private AttributionResultRuleMetadata(short id, String name, FieldType type, boolean isRequired, 
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
