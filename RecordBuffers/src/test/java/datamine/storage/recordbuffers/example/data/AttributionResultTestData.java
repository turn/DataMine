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
public class AttributionResultTestData extends AbstractTestData<AttributionResultInterface, AttributionResultMetadata> {

    public AttributionResultTestData(List<EnumMap<AttributionResultMetadata, Object>> input) {
        super(input);
    }

    @Override
    public List<AttributionResultInterface> createObjects(RecordBuilderInterface builder) {
		List<AttributionResultInterface> records = Lists.newArrayList();
		for (EnumMap<AttributionResultMetadata, Object> cur : data) {
			AttributionResultInterface record = builder.build(AttributionResultInterface.class);
			
			if (cur.containsKey(AttributionResultMetadata.ID)) {
				record.setId((Integer) cur.get(AttributionResultMetadata.ID));
			}

			if (cur.containsKey(AttributionResultMetadata.RULES)) {
				record.setRules(new AttributionResultRuleTestData((List) cur.get(AttributionResultMetadata.RULES)).createObjects(builder));
			}

			records.add(record);
		}
		return records;
    }

    @Override
    public void assertObjects(List<AttributionResultInterface> objectList) {
		int size = objectList.size();
		Assert.assertEquals(size, data.size());
		for (int i = 0; i < size; ++i) {
			
			if (data.get(i).containsKey(AttributionResultMetadata.ID)) {
				Assert.assertEquals(objectList.get(i).getId(), data.get(i).get(AttributionResultMetadata.ID));
			}

			if (data.get(i).containsKey(AttributionResultMetadata.RULES)) {
				new AttributionResultRuleTestData((List) data.get(i).get(AttributionResultMetadata.RULES)).assertObjects(objectList.get(i).getRules());
			}

		}
	}

    public static List<EnumMap<AttributionResultMetadata, Object>> createInputData(int num) {
		List<EnumMap<AttributionResultMetadata, Object>> dataList = Lists.newArrayList();
		for (int i = 0; i < num; ++i) {
			EnumMap<AttributionResultMetadata, Object> dataMap = Maps.newEnumMap(AttributionResultMetadata.class);
			
			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)AttributionResultMetadata.ID.getField().getType()).getType());
				if (val != null) {
					dataMap.put(AttributionResultMetadata.ID, val);
				}
			}

			{
				Object val = AttributionResultRuleTestData.createInputData(3);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(AttributionResultMetadata.RULES, val);
				}
			}

			if (!dataMap.isEmpty()) {
				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}


