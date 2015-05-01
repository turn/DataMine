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
public class ProviderUserIdTestData extends AbstractTestData<ProviderUserIdInterface, ProviderUserIdMetadata> {

    public ProviderUserIdTestData(List<EnumMap<ProviderUserIdMetadata, Object>> input) {
        super(input);
    }

    @Override
    public List<ProviderUserIdInterface> createObjects(RecordBuilderInterface builder) {
		List<ProviderUserIdInterface> records = Lists.newArrayList();
		for (EnumMap<ProviderUserIdMetadata, Object> cur : data) {
			ProviderUserIdInterface record = builder.build(ProviderUserIdInterface.class);
			
			if (cur.containsKey(ProviderUserIdMetadata.PROVIDER_TYPE)) {
				record.setProviderType((Byte) cur.get(ProviderUserIdMetadata.PROVIDER_TYPE));
			}

			if (cur.containsKey(ProviderUserIdMetadata.PROVIDER_ID)) {
				record.setProviderId((Integer) cur.get(ProviderUserIdMetadata.PROVIDER_ID));
			}

			records.add(record);
		}
		return records;
    }

    @Override
    public void assertObjects(List<ProviderUserIdInterface> objectList) {
		int size = objectList.size();
		Assert.assertEquals(size, data.size());
		for (int i = 0; i < size; ++i) {
			
			if (data.get(i).containsKey(ProviderUserIdMetadata.PROVIDER_TYPE)) {
				Assert.assertEquals(objectList.get(i).getProviderType(), data.get(i).get(ProviderUserIdMetadata.PROVIDER_TYPE));
			}

			if (data.get(i).containsKey(ProviderUserIdMetadata.PROVIDER_ID)) {
				Assert.assertEquals(objectList.get(i).getProviderId(), data.get(i).get(ProviderUserIdMetadata.PROVIDER_ID));
			}

		}
	}

    public static List<EnumMap<ProviderUserIdMetadata, Object>> createInputData(int num) {
		List<EnumMap<ProviderUserIdMetadata, Object>> dataList = Lists.newArrayList();
		for (int i = 0; i < num; ++i) {
			EnumMap<ProviderUserIdMetadata, Object> dataMap = Maps.newEnumMap(ProviderUserIdMetadata.class);
			
			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)ProviderUserIdMetadata.PROVIDER_TYPE.getField().getType()).getType());
				if (val != null) {
					dataMap.put(ProviderUserIdMetadata.PROVIDER_TYPE, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)ProviderUserIdMetadata.PROVIDER_ID.getField().getType()).getType());
				if (val != null) {
					dataMap.put(ProviderUserIdMetadata.PROVIDER_ID, val);
				}
			}

			if (!dataMap.isEmpty()) {
				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}


