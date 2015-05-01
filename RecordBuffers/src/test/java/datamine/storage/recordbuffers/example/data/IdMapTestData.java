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
public class IdMapTestData extends AbstractTestData<IdMapInterface, IdMapMetadata> {

    public IdMapTestData(List<EnumMap<IdMapMetadata, Object>> input) {
        super(input);
    }

    @Override
    public List<IdMapInterface> createObjects(RecordBuilderInterface builder) {
		List<IdMapInterface> records = Lists.newArrayList();
		for (EnumMap<IdMapMetadata, Object> cur : data) {
			IdMapInterface record = builder.build(IdMapInterface.class);
			
			if (cur.containsKey(IdMapMetadata.MEDIA_PROVIDER_IDS)) {
				record.setMediaProviderIds(new ProviderUserIdTestData((List) cur.get(IdMapMetadata.MEDIA_PROVIDER_IDS)).createObjects(builder));
			}

			records.add(record);
		}
		return records;
    }

    @Override
    public void assertObjects(List<IdMapInterface> objectList) {
		int size = objectList.size();
		Assert.assertEquals(size, data.size());
		for (int i = 0; i < size; ++i) {
			
			if (data.get(i).containsKey(IdMapMetadata.MEDIA_PROVIDER_IDS)) {
				new ProviderUserIdTestData((List) data.get(i).get(IdMapMetadata.MEDIA_PROVIDER_IDS)).assertObjects(objectList.get(i).getMediaProviderIds());
			}

		}
	}

    public static List<EnumMap<IdMapMetadata, Object>> createInputData(int num) {
		List<EnumMap<IdMapMetadata, Object>> dataList = Lists.newArrayList();
		for (int i = 0; i < num; ++i) {
			EnumMap<IdMapMetadata, Object> dataMap = Maps.newEnumMap(IdMapMetadata.class);
			
			{
				Object val = ProviderUserIdTestData.createInputData(3);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(IdMapMetadata.MEDIA_PROVIDER_IDS, val);
				}
			}

			if (!dataMap.isEmpty()) {
				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}


