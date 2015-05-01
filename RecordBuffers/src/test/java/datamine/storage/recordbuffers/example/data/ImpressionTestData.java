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
public class ImpressionTestData extends AbstractTestData<ImpressionInterface, ImpressionMetadata> {

    public ImpressionTestData(List<EnumMap<ImpressionMetadata, Object>> input) {
        super(input);
    }

    @Override
    public List<ImpressionInterface> createObjects(RecordBuilderInterface builder) {
		List<ImpressionInterface> records = Lists.newArrayList();
		for (EnumMap<ImpressionMetadata, Object> cur : data) {
			ImpressionInterface record = builder.build(ImpressionInterface.class);
			
			if (cur.containsKey(ImpressionMetadata.MEDIA_PROVIDER_ID)) {
				record.setMediaProviderId((Integer) cur.get(ImpressionMetadata.MEDIA_PROVIDER_ID));
			}

			if (cur.containsKey(ImpressionMetadata.MP_TPT_CATEGORY_ID)) {
				record.setMpTptCategoryId((Short) cur.get(ImpressionMetadata.MP_TPT_CATEGORY_ID));
			}

			if (cur.containsKey(ImpressionMetadata.TRUNCATED_URL)) {
				record.setTruncatedUrl((String) cur.get(ImpressionMetadata.TRUNCATED_URL));
			}

			if (cur.containsKey(ImpressionMetadata.BID)) {
				record.setBid((Boolean) cur.get(ImpressionMetadata.BID));
			}

			if (cur.containsKey(ImpressionMetadata.BID_TYPE)) {
				record.setBidType((Byte) cur.get(ImpressionMetadata.BID_TYPE));
			}

			if (cur.containsKey(ImpressionMetadata.ATTRIBUTION_RESULTS)) {
				record.setAttributionResults(new AttributionResultTestData((List) cur.get(ImpressionMetadata.ATTRIBUTION_RESULTS)).createObjects(builder));
			}

			if (cur.containsKey(ImpressionMetadata.ALLOWED_AD_FORMATS)) {
				record.setAllowedAdFormats((Long) cur.get(ImpressionMetadata.ALLOWED_AD_FORMATS));
			}

			if (cur.containsKey(ImpressionMetadata.COST)) {
				record.setCost((Double) cur.get(ImpressionMetadata.COST));
			}

			records.add(record);
		}
		return records;
    }

    @Override
    public void assertObjects(List<ImpressionInterface> objectList) {
		int size = objectList.size();
		Assert.assertEquals(size, data.size());
		for (int i = 0; i < size; ++i) {
			
			if (data.get(i).containsKey(ImpressionMetadata.MEDIA_PROVIDER_ID)) {
				Assert.assertEquals(objectList.get(i).getMediaProviderId(), data.get(i).get(ImpressionMetadata.MEDIA_PROVIDER_ID));
			}

			if (data.get(i).containsKey(ImpressionMetadata.MP_TPT_CATEGORY_ID)) {
				Assert.assertEquals(objectList.get(i).getMpTptCategoryId(), data.get(i).get(ImpressionMetadata.MP_TPT_CATEGORY_ID));
			}

			if (data.get(i).containsKey(ImpressionMetadata.TRUNCATED_URL)) {
				Assert.assertEquals(objectList.get(i).getTruncatedUrl(), data.get(i).get(ImpressionMetadata.TRUNCATED_URL));
			}

			if (data.get(i).containsKey(ImpressionMetadata.BID)) {
				Assert.assertEquals(objectList.get(i).isBid(), data.get(i).get(ImpressionMetadata.BID));
			}

			if (data.get(i).containsKey(ImpressionMetadata.BID_TYPE)) {
				Assert.assertEquals(objectList.get(i).getBidType(), data.get(i).get(ImpressionMetadata.BID_TYPE));
			}

			if (data.get(i).containsKey(ImpressionMetadata.ATTRIBUTION_RESULTS)) {
				new AttributionResultTestData((List) data.get(i).get(ImpressionMetadata.ATTRIBUTION_RESULTS)).assertObjects(objectList.get(i).getAttributionResults());
			}

			if (data.get(i).containsKey(ImpressionMetadata.ALLOWED_AD_FORMATS)) {
				Assert.assertEquals(objectList.get(i).getAllowedAdFormats(), data.get(i).get(ImpressionMetadata.ALLOWED_AD_FORMATS));
			}

			if (data.get(i).containsKey(ImpressionMetadata.COST)) {
				Assert.assertEquals(objectList.get(i).getCost(), data.get(i).get(ImpressionMetadata.COST));
			}

		}
	}

    public static List<EnumMap<ImpressionMetadata, Object>> createInputData(int num) {
		List<EnumMap<ImpressionMetadata, Object>> dataList = Lists.newArrayList();
		for (int i = 0; i < num; ++i) {
			EnumMap<ImpressionMetadata, Object> dataMap = Maps.newEnumMap(ImpressionMetadata.class);
			
			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)ImpressionMetadata.MEDIA_PROVIDER_ID.getField().getType()).getType());
				if (val != null) {
					dataMap.put(ImpressionMetadata.MEDIA_PROVIDER_ID, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)ImpressionMetadata.MP_TPT_CATEGORY_ID.getField().getType()).getType());
				if (val != null) {
					dataMap.put(ImpressionMetadata.MP_TPT_CATEGORY_ID, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)ImpressionMetadata.TRUNCATED_URL.getField().getType()).getType());
				if (val != null) {
					dataMap.put(ImpressionMetadata.TRUNCATED_URL, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)ImpressionMetadata.BID.getField().getType()).getType());
				if (val != null) {
					dataMap.put(ImpressionMetadata.BID, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)ImpressionMetadata.BID_TYPE.getField().getType()).getType());
				if (val != null) {
					dataMap.put(ImpressionMetadata.BID_TYPE, val);
				}
			}

			{
				Object val = AttributionResultTestData.createInputData(3);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(ImpressionMetadata.ATTRIBUTION_RESULTS, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)ImpressionMetadata.ALLOWED_AD_FORMATS.getField().getType()).getType());
				if (val != null) {
					dataMap.put(ImpressionMetadata.ALLOWED_AD_FORMATS, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)ImpressionMetadata.COST.getField().getType()).getType());
				if (val != null) {
					dataMap.put(ImpressionMetadata.COST, val);
				}
			}

			if (!dataMap.isEmpty()) {
				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}


