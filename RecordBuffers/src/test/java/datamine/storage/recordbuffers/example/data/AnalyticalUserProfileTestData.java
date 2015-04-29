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
public class AnalyticalUserProfileTestData extends AbstractTestData<AnalyticalUserProfileInterface, AnalyticalUserProfileMetadata> {

    public AnalyticalUserProfileTestData(List<EnumMap<AnalyticalUserProfileMetadata, Object>> input) {
        super(input);
    }

    @Override
    public List<AnalyticalUserProfileInterface> createObjects(RecordBuilderInterface builder) {
		List<AnalyticalUserProfileInterface> records = Lists.newArrayList();
		for (EnumMap<AnalyticalUserProfileMetadata, Object> cur : data) {
			AnalyticalUserProfileInterface record = builder.build(AnalyticalUserProfileInterface.class);
			
			if (cur.containsKey(AnalyticalUserProfileMetadata.USER_ID)) {
				record.setUserId((Long) cur.get(AnalyticalUserProfileMetadata.USER_ID));
			}

			if (cur.containsKey(AnalyticalUserProfileMetadata.VERSION)) {
				record.setVersion((Byte) cur.get(AnalyticalUserProfileMetadata.VERSION));
			}

			if (cur.containsKey(AnalyticalUserProfileMetadata.RESOLUTION)) {
				record.setResolution((Short) cur.get(AnalyticalUserProfileMetadata.RESOLUTION));
			}

			if (cur.containsKey(AnalyticalUserProfileMetadata.OS_VERSION)) {
				record.setOsVersion((String) cur.get(AnalyticalUserProfileMetadata.OS_VERSION));
			}

			if (cur.containsKey(AnalyticalUserProfileMetadata.IMPRESSIONS)) {
				record.setImpressions(new ImpressionTestData((List) cur.get(AnalyticalUserProfileMetadata.IMPRESSIONS)).createObjects(builder));
			}

			if (cur.containsKey(AnalyticalUserProfileMetadata.ID_MAPS)) {
				record.setIdMaps(new IdMapTestData((List) cur.get(AnalyticalUserProfileMetadata.ID_MAPS)).createObjects(builder).get(0));
			}

			if (cur.containsKey(AnalyticalUserProfileMetadata.TIME_LIST)) {
				record.setTimeList((List) cur.get(AnalyticalUserProfileMetadata.TIME_LIST));
			}

			records.add(record);
		}
		return records;
    }

    @Override
    public void assertObjects(List<AnalyticalUserProfileInterface> objectList) {
		int size = objectList.size();
		Assert.assertEquals(size, data.size());
		for (int i = 0; i < size; ++i) {
			
			if (data.get(i).containsKey(AnalyticalUserProfileMetadata.USER_ID)) {
				Assert.assertEquals(objectList.get(i).getUserId(), data.get(i).get(AnalyticalUserProfileMetadata.USER_ID));
			}

			if (data.get(i).containsKey(AnalyticalUserProfileMetadata.VERSION)) {
				Assert.assertEquals(objectList.get(i).getVersion(), data.get(i).get(AnalyticalUserProfileMetadata.VERSION));
			}

			if (data.get(i).containsKey(AnalyticalUserProfileMetadata.RESOLUTION)) {
				Assert.assertEquals(objectList.get(i).getResolution(), data.get(i).get(AnalyticalUserProfileMetadata.RESOLUTION));
			}

			if (data.get(i).containsKey(AnalyticalUserProfileMetadata.OS_VERSION)) {
				Assert.assertEquals(objectList.get(i).getOsVersion(), data.get(i).get(AnalyticalUserProfileMetadata.OS_VERSION));
			}

			if (data.get(i).containsKey(AnalyticalUserProfileMetadata.IMPRESSIONS)) {
				new ImpressionTestData((List) data.get(i).get(AnalyticalUserProfileMetadata.IMPRESSIONS)).assertObjects(objectList.get(i).getImpressions());
			}

			if (data.get(i).containsKey(AnalyticalUserProfileMetadata.ID_MAPS)) {
				new IdMapTestData((List) data.get(i).get(AnalyticalUserProfileMetadata.ID_MAPS)).assertObjects(Lists.newArrayList(objectList.get(i).getIdMaps()));
			}

			if (data.get(i).containsKey(AnalyticalUserProfileMetadata.TIME_LIST)) {
				Assert.assertEquals((List) objectList.get(i).getTimeList(), data.get(i).get(AnalyticalUserProfileMetadata.TIME_LIST));
			}

		}
	}

    public static List<EnumMap<AnalyticalUserProfileMetadata, Object>> createInputData(int num) {
		List<EnumMap<AnalyticalUserProfileMetadata, Object>> dataList = Lists.newArrayList();
		for (int i = 0; i < num; ++i) {
			EnumMap<AnalyticalUserProfileMetadata, Object> dataMap = Maps.newEnumMap(AnalyticalUserProfileMetadata.class);
			
			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)AnalyticalUserProfileMetadata.USER_ID.getField().getType()).getType());
				if (val != null) {
					dataMap.put(AnalyticalUserProfileMetadata.USER_ID, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)AnalyticalUserProfileMetadata.VERSION.getField().getType()).getType());
				if (val != null) {
					dataMap.put(AnalyticalUserProfileMetadata.VERSION, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)AnalyticalUserProfileMetadata.RESOLUTION.getField().getType()).getType());
				if (val != null) {
					dataMap.put(AnalyticalUserProfileMetadata.RESOLUTION, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)AnalyticalUserProfileMetadata.OS_VERSION.getField().getType()).getType());
				if (val != null) {
					dataMap.put(AnalyticalUserProfileMetadata.OS_VERSION, val);
				}
			}

			{
				Object val = ImpressionTestData.createInputData(3);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(AnalyticalUserProfileMetadata.IMPRESSIONS, val);
				}
			}

			{
				Object val = IdMapTestData.createInputData(1);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(AnalyticalUserProfileMetadata.ID_MAPS, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueArrayOf(((PrimitiveFieldType) ((CollectionFieldType)AnalyticalUserProfileMetadata.TIME_LIST.getField().getType()).getElementType()).getType(), num);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(AnalyticalUserProfileMetadata.TIME_LIST, val);
				}
			}

			if (!dataMap.isEmpty()) {
				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}


