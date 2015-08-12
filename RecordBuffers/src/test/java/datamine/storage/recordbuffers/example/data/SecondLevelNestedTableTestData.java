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
package datamine.storage.recordbuffers.example.data;

import datamine.storage.api.RecordBuilderInterface;
import datamine.storage.recordbuffers.example.model.*;
import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.storage.idl.generator.AbstractTestData;
import datamine.storage.idl.generator.RandomValueGenerator;
import datamine.storage.idl.type.*;

import java.nio.ByteBuffer;
import java.util.*;

import org.testng.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;



/**
 * DO NOT CHANGE! Auto-generated code
 */
public class SecondLevelNestedTableTestData extends AbstractTestData<SecondLevelNestedTableInterface, SecondLevelNestedTableMetadata> {

    public SecondLevelNestedTableTestData(List<EnumMap<SecondLevelNestedTableMetadata, Object>> input) {
        super(input);
    }

    @Override
    public List<SecondLevelNestedTableInterface> createObjects(RecordBuilderInterface builder) {
		List<SecondLevelNestedTableInterface> records = Lists.newArrayList();
		for (EnumMap<SecondLevelNestedTableMetadata, Object> cur : data) {
			SecondLevelNestedTableInterface record = builder.build(SecondLevelNestedTableInterface.class);
			
			if (cur.containsKey(SecondLevelNestedTableMetadata.BYTE_REQUIRED_COLUMN)) {
				record.setByteRequiredColumn((Byte) cur.get(SecondLevelNestedTableMetadata.BYTE_REQUIRED_COLUMN));
			}

			if (cur.containsKey(SecondLevelNestedTableMetadata.BOOLEAN_LIST_COLUMN)) {
				record.setBooleanListColumn((List) cur.get(SecondLevelNestedTableMetadata.BOOLEAN_LIST_COLUMN));
			}

			records.add(record);
		}
		return records;
    }

    @Override
    public void assertObjects(List<SecondLevelNestedTableInterface> objectList) {
		int size = objectList.size();
		Assert.assertEquals(size, data.size());
		for (int i = 0; i < size; ++i) {
			
			if (data.get(i).containsKey(SecondLevelNestedTableMetadata.BYTE_REQUIRED_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getByteRequiredColumn(), data.get(i).get(SecondLevelNestedTableMetadata.BYTE_REQUIRED_COLUMN));
			}

			if (data.get(i).containsKey(SecondLevelNestedTableMetadata.BOOLEAN_LIST_COLUMN)) {
				Assert.assertEquals((List) objectList.get(i).getBooleanListColumn(), data.get(i).get(SecondLevelNestedTableMetadata.BOOLEAN_LIST_COLUMN));
			}

		}
	}

    public static List<EnumMap<SecondLevelNestedTableMetadata, Object>> createInputData(int num) {
		List<EnumMap<SecondLevelNestedTableMetadata, Object>> dataList = Lists.newArrayList();
		for (int i = 0; i < num; ++i) {
			EnumMap<SecondLevelNestedTableMetadata, Object> dataMap = Maps.newEnumMap(SecondLevelNestedTableMetadata.class);
			
			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)SecondLevelNestedTableMetadata.BYTE_REQUIRED_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(SecondLevelNestedTableMetadata.BYTE_REQUIRED_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueArrayOf(((PrimitiveFieldType) ((CollectionFieldType)SecondLevelNestedTableMetadata.BOOLEAN_LIST_COLUMN.getField().getType()).getElementType()).getPrimitiveType(), num);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(SecondLevelNestedTableMetadata.BOOLEAN_LIST_COLUMN, val);
				}
			}

			if (!dataMap.isEmpty()) {
				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}


