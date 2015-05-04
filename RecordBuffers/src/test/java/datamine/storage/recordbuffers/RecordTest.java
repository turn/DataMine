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
package datamine.storage.recordbuffers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Lists;

import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.recordbuffers.Record;
import datamine.storage.recordbuffers.RecordBuffer;
import datamine.storage.recordbuffers.example.data.AnalyticalUserProfileTestData;
import datamine.storage.recordbuffers.example.interfaces.AnalyticalUserProfileInterface;
import datamine.storage.recordbuffers.example.interfaces.AttributionResultInterface;
import datamine.storage.recordbuffers.example.interfaces.ImpressionInterface;
import datamine.storage.recordbuffers.example.model.AnalyticalUserProfileMetadata;
import datamine.storage.recordbuffers.example.wrapper.AnalyticalUserProfileRecord;
import datamine.storage.recordbuffers.example.wrapper.ImpressionRecord;
import datamine.storage.recordbuffers.example.wrapper.builder.RecordBuffersBuilder;
import datamine.storage.recordbuffers.idl.value.FieldValueOperatorFactory;

public class RecordTest {

	private AnalyticalUserProfileTestData testAupData;
	private List<AnalyticalUserProfileInterface> testAupList;
	private Record<AnalyticalUserProfileMetadata> testRecord;
	@SuppressWarnings("rawtypes")
	private List<Record> recordList = Lists.newArrayList();
	private int testAupNum = 3;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeMethod
	private void prepareRecord() {
		testAupData = new AnalyticalUserProfileTestData(
				AnalyticalUserProfileTestData.createInputData(testAupNum));
		testAupList = testAupData.createObjects(new RecordBuffersBuilder());
		// 
		recordList.clear();
		for (AnalyticalUserProfileInterface cur : testAupList) {
			Record<AnalyticalUserProfileMetadata> record = (Record<AnalyticalUserProfileMetadata>) cur.getBaseObject();
			recordList.add(new Record(AnalyticalUserProfileMetadata.class, record.getRecordBuffer()));
		}
		testRecord = recordList.get(0);
	}

	@Test
	public void getRecordLength() {
		GroupFieldType gft = new GroupFieldType("AUP", AnalyticalUserProfileMetadata.class);
		int len = FieldValueOperatorFactory.getOperator(gft).getNumOfBytes(testRecord);
		// trigger the creation of the object array
		testRecord.getValue(AnalyticalUserProfileMetadata.IMPRESSIONS);
		Assert.assertEquals(testRecord.getNumOfBytes(), len);
	}
	
	@Test
	public void recordCreation() {
		// create the record
		AnalyticalUserProfileInterface aup = new AnalyticalUserProfileRecord();
		aup.setOsVersion("1234567890");
		aup.setResolution((short) 100);
		aup.setUserId(123456);
		aup.setVersion((byte) 123);
		List<Integer> ids = Lists.newArrayList();
		Random rand = new Random();
		for (int i=0; i<10; ++i) {
			ids.add(rand.nextInt());
		}
		aup.setTimeList(ids);
		ImpressionInterface imp = new ImpressionRecord();
		imp.setAllowedAdFormats(11111111111111L);
		imp.setBid(false);
		imp.setBidType((byte) 14);
		imp.setCost(0.0123);
		imp.setMediaProviderId(9);
		imp.setMpTptCategoryId((short) 4321);
		imp.setTruncatedUrl("asfaweraf___234#$@%_");
		imp.setAttributionResults(new ArrayList<AttributionResultInterface>());
		List<ImpressionInterface> imps = Lists.newArrayList();
		imps.add(imp);
		aup.setImpressions(imps);
		@SuppressWarnings("unchecked")
		Record<AnalyticalUserProfileMetadata> aupRecord = (Record<AnalyticalUserProfileMetadata>) aup.getBaseObject();
		// start testing
		int len = aupRecord.getNumOfBytes();
		int impSize = aupRecord.getListSize(AnalyticalUserProfileMetadata.IMPRESSIONS);
		int idSize = aupRecord.getListSize(AnalyticalUserProfileMetadata.TIME_LIST);
		aupRecord.getValue(AnalyticalUserProfileMetadata.ID_MAPS);
		
		Assert.assertEquals(impSize, aup.getImpressionsSize());
		Assert.assertEquals(idSize, aup.getTimeList().size());
		
		GroupFieldType gft = new GroupFieldType("AUP", AnalyticalUserProfileMetadata.class);
		Assert.assertEquals(len, FieldValueOperatorFactory.getOperator(gft).getNumOfBytes(aupRecord));
	}
	
	
	@Test
	public void getListSize() {
		Assert.assertEquals(testAupNum, testAupList.size());
		Assert.assertEquals(testAupNum, testRecord.getListSize(AnalyticalUserProfileMetadata.IMPRESSIONS));
	}

	@Test
	public void getRecordBuffer() {
		RecordBuffer buffer = testRecord.getRecordBuffer();
		Assert.assertEquals(buffer.getRecordBufferSize(), buffer.getByteBuffer().limit());
	}

	@Test
	public void getValue() {
		Collections.sort(testAupList);
		
		Assert.assertTrue(testRecord.getValue(AnalyticalUserProfileMetadata.USER_ID) != null);		
		
		Assert.assertTrue((byte)testAupList.get(0).getVersion() >= (byte)testAupList.get(1).getVersion());
		Assert.assertTrue((byte)testAupList.get(1).getVersion() >= (byte)testAupList.get(2).getVersion());
		
		@SuppressWarnings("unchecked")
		List<Object> imps = (List<Object>) testRecord.getValue(AnalyticalUserProfileMetadata.IMPRESSIONS);
		Assert.assertEquals(imps.size(), testAupNum);
		
		Assert.assertTrue(testRecord.getValue(AnalyticalUserProfileMetadata.OS_VERSION) instanceof String);
	}

	@Test
	public void setValue() {
		testRecord.setValue(AnalyticalUserProfileMetadata.USER_ID, 123L);
		Assert.assertEquals(testRecord.getValue(AnalyticalUserProfileMetadata.USER_ID), 123L);	
	}
	
	@Test
	public void assertAll() {
		testAupData.assertObjects(testAupList);
	}
	
//	@Test
	public void testPerformance() {
		for (int i=0; i<10000; ++i) {
			prepareRecord();
			getListSize();
			prepareRecord();
			getRecordBuffer();
			prepareRecord();
			getValue();
			prepareRecord();
			setValue();
		}
	}
}
