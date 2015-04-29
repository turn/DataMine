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

import java.nio.ByteBuffer;
import java.util.*;

import org.testng.Assert;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import datamine.storage.api.RecordBuilderInterface;
import datamine.storage.idl.generator.AbstractTestData;
import datamine.storage.idl.generator.RandomValueGenerator;
import datamine.storage.idl.type.*;
import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.storage.recordbuffers.example.model.*;



/**
 * DO NOT CHANGE! Auto-generated code
 */
public class AttributionResultRuleTestData extends AbstractTestData<AttributionResultRuleInterface, AttributionResultRuleMetadata> {

    public AttributionResultRuleTestData(List<EnumMap<AttributionResultRuleMetadata, Object>> input) {
        super(input);
    }

    @Override
    public List<AttributionResultRuleInterface> createObjects(RecordBuilderInterface builder) {
		List<AttributionResultRuleInterface> records = Lists.newArrayList();
		for (EnumMap<AttributionResultRuleMetadata, Object> cur : data) {
			AttributionResultRuleInterface record = builder.build(AttributionResultRuleInterface.class);
			
			if (cur.containsKey(AttributionResultRuleMetadata.RUN_NUM)) {
				record.setRunNum((Byte) cur.get(AttributionResultRuleMetadata.RUN_NUM));
			}

			if (cur.containsKey(AttributionResultRuleMetadata.VALUE)) {
				record.setValue((String) cur.get(AttributionResultRuleMetadata.VALUE));
			}

			records.add(record);
		}
		return records;
    }

    @Override
    public void assertObjects(List<AttributionResultRuleInterface> objectList) {
		int size = objectList.size();
		Assert.assertEquals(size, data.size());
		for (int i = 0; i < size; ++i) {
			
			if (data.get(i).containsKey(AttributionResultRuleMetadata.RUN_NUM)) {
				Assert.assertEquals(objectList.get(i).getRunNum(), data.get(i).get(AttributionResultRuleMetadata.RUN_NUM));
			}

			if (data.get(i).containsKey(AttributionResultRuleMetadata.VALUE)) {
				Assert.assertEquals(objectList.get(i).getValue(), data.get(i).get(AttributionResultRuleMetadata.VALUE));
			}

		}
	}

    public static List<EnumMap<AttributionResultRuleMetadata, Object>> createInputData(int num) {
		List<EnumMap<AttributionResultRuleMetadata, Object>> dataList = Lists.newArrayList();
		for (int i = 0; i < num; ++i) {
			EnumMap<AttributionResultRuleMetadata, Object> dataMap = Maps.newEnumMap(AttributionResultRuleMetadata.class);
			
			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)AttributionResultRuleMetadata.RUN_NUM.getField().getType()).getType());
				if (val != null) {
					dataMap.put(AttributionResultRuleMetadata.RUN_NUM, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)AttributionResultRuleMetadata.VALUE.getField().getType()).getType());
				if (val != null) {
					dataMap.put(AttributionResultRuleMetadata.VALUE, val);
				}
			}

			if (!dataMap.isEmpty()) {
				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}


