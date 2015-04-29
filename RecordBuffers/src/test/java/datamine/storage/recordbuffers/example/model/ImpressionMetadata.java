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
public enum ImpressionMetadata implements RecordMetadataInterface {

	MEDIA_PROVIDER_ID((short)1, "media_provider_id", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32), true, null),
	MP_TPT_CATEGORY_ID((short)2, "mp_tpt_category_id", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT16), false, (short)-1),
	TRUNCATED_URL((short)3, "truncated_url", FieldTypeFactory.getPrimitiveType(PrimitiveType.STRING), false, (String)"Unknown"),
	BID((short)4, "bid", FieldTypeFactory.getPrimitiveType(PrimitiveType.BOOL), false, (boolean)true),
	BID_TYPE((short)5, "bid_type", FieldTypeFactory.getPrimitiveType(PrimitiveType.BYTE), false, (byte)-1),
	ATTRIBUTION_RESULTS((short)6, "attribution_results", FieldTypeFactory.getListType(FieldTypeFactory.getGroupType(AttributionResultMetadata.class)), false, null),
	ALLOWED_AD_FORMATS((short)7, "allowed_ad_formats", FieldTypeFactory.getPrimitiveType(PrimitiveType.INT64), false, (long)-1),
	COST((short)8, "cost", FieldTypeFactory.getPrimitiveType(PrimitiveType.DOUBLE), false, (double)0.0),
;

	static final short version = 1;
	static final String name = "impression";
	private Field field;

	private ImpressionMetadata(short id, String name, FieldType type, boolean isRequired, Object defaultValue) {
		this(id,name, type, isRequired, defaultValue, false, false, false);
	}

	private ImpressionMetadata(short id, String name, FieldType type, boolean isRequired, 
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

