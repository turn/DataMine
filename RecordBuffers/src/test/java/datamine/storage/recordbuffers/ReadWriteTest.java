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

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.recordbuffers.example.data.MainTableTestData;
import datamine.storage.recordbuffers.example.derived.FirstLevelNestedTableDerived;
import datamine.storage.recordbuffers.example.derived.MainTableDerived;
import datamine.storage.recordbuffers.example.interfaces.FirstLevelNestedTableDerivedValueInterface;
import datamine.storage.recordbuffers.example.interfaces.FirstLevelNestedTableInterface;
import datamine.storage.recordbuffers.example.interfaces.MainTableDerivedValueInterface;
import datamine.storage.recordbuffers.example.interfaces.MainTableInterface;
import datamine.storage.recordbuffers.example.interfaces.SecondLevelNestedTableInterface;
import datamine.storage.recordbuffers.example.interfaces.StructTableInterface;
import datamine.storage.recordbuffers.example.model.MainTableMetadata;
import datamine.storage.recordbuffers.example.printers.MainTableInterfaceContentPrinter;
import datamine.storage.recordbuffers.example.wrapper.FirstLevelNestedTableRecord;
import datamine.storage.recordbuffers.example.wrapper.MainTableRecord;
import datamine.storage.recordbuffers.example.wrapper.SecondLevelNestedTableRecord;
import datamine.storage.recordbuffers.example.wrapper.StructTableRecord;
import datamine.storage.recordbuffers.example.wrapper.builder.RecordBuffersBuilder;
import datamine.storage.recordbuffers.idl.value.FieldValueOperatorFactory;

public class ReadWriteTest {

	private MainTableTestData mainTableTestData;
	private List<MainTableInterface> mainTableList;
	private Record<MainTableMetadata> mainTableRecord;
	@SuppressWarnings("rawtypes")
	private List<Record> recordList = Lists.newArrayList();
	private int recordNum = 3;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@BeforeMethod
	private void prepareRecord() {

		mainTableTestData = new MainTableTestData(MainTableTestData.createInputData(recordNum));
		mainTableList = mainTableTestData.createObjects(new RecordBuffersBuilder());
		recordList.clear();
		for (MainTableInterface cur : mainTableList) {
			Record<MainTableMetadata> record = (Record<MainTableMetadata>) cur.getBaseObject();
			recordList.add(new ReadOnlyRecord(MainTableMetadata.class, record.getRecordBuffer()));
		}
		mainTableRecord = recordList.get(0);
	}

	@Test
	public void getRecordLength() {
		GroupFieldType gft = new GroupFieldType("main_table", MainTableMetadata.class);
		int len = FieldValueOperatorFactory.getOperator(gft).getNumOfBytes(mainTableRecord);

		// trigger the creation of the object array
		mainTableRecord.getValue(MainTableMetadata.NESTED_TABLE_COLUMN);
		Assert.assertEquals(mainTableRecord.getNumOfBytes(), len);
	}

	@Test
	public void getListSize() {
		Assert.assertEquals(recordNum, mainTableList.size());
		Assert.assertEquals(recordNum, mainTableRecord.getListSize(MainTableMetadata.NESTED_TABLE_COLUMN));
	}

	@Test
	public void getRecordBuffer() {
		RecordBuffer buffer = mainTableRecord.getRecordBuffer();
		Assert.assertEquals(buffer.getRecordBufferSize(), buffer.getByteBuffer().limit());
	}

	@Test
	public void getValue() {
		Collections.sort(mainTableList);

		Assert.assertTrue(mainTableRecord.getValue(MainTableMetadata.LONG_REQUIRED_COLUMN) != null);		

		Assert.assertTrue(mainTableList.get(0).getIntSortedColumn() >= mainTableList.get(1).getIntSortedColumn());
		Assert.assertTrue(mainTableList.get(1).getIntSortedColumn() >= mainTableList.get(2).getIntSortedColumn());

		@SuppressWarnings("unchecked")
		List<Object> imps = (List<Object>) mainTableRecord.getValue(MainTableMetadata.NESTED_TABLE_COLUMN);
		Assert.assertEquals(imps.size(), recordNum);

		Assert.assertTrue(mainTableRecord.getValue(MainTableMetadata.STRING_COLUMN) instanceof String);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void setValue() {
		Record record = (Record) mainTableList.get(0).getBaseObject();
		record.setValue(MainTableMetadata.LONG_REQUIRED_COLUMN, 123L);
		Assert.assertEquals(record.getValue(MainTableMetadata.LONG_REQUIRED_COLUMN), 123L);	
	}

	@Test
	public void assertAll() {
		mainTableTestData.assertObjects(mainTableList);
	}

	@Test
	public void derivedFields() {
		for (MainTableInterface cur : mainTableList) {
			Assert.assertEquals(cur.getStringDerivedColumn(),
					cur.getStringDerivedColumnDefaultValue());

			MainTableDerivedValueInterface derivedImpl = new MainTableDerived(cur);
			cur.setDerivedValueImplementation(derivedImpl);
			Assert.assertEquals("StringDerivedColumn@MainTable", derivedImpl.getStringDerivedColumn());
			Assert.assertEquals(101, derivedImpl.getIntDerivedColumn());

			for(FirstLevelNestedTableInterface imp : cur.getNestedTableColumn()) {
				FirstLevelNestedTableDerivedValueInterface impDev = new
						FirstLevelNestedTableDerived(imp);
				imp.setDerivedValueImplementation(impDev);
				Assert.assertEquals("StringDerivedColumn@FirstLevelNestedTable", 
						imp.getStringDerivedColumn());
			}
		}
	}

	private MainTableInterface createRecord() {
		// create the record
		MainTableInterface aup = new MainTableRecord();
		aup.setStringColumn("1234567890");
		aup.setShortColumn((short) 100);
		aup.setLongRequiredColumn(123456);
		aup.setByteColumn((byte) 123);
		aup.setBooleanColumn(true);
		aup.setIntSortedColumn(98);
		aup.setFloatColumn(0.1f);
		aup.setDoubleColumn(0.0012130234234);
		List<Integer> ids = Lists.newArrayList();
		Random rand = new Random();
		for (int i=0; i<10; ++i) {
			ids.add(rand.nextInt());
		}
		aup.setIntListColumn(ids);

		FirstLevelNestedTableInterface nestedTable = new FirstLevelNestedTableRecord();
		nestedTable.setIntRequiredColumn(202);
		SecondLevelNestedTableInterface snt = new SecondLevelNestedTableRecord();
		snt.setByteRequiredColumn((byte)100);
		List<Boolean> bs = Lists.newArrayList();
		for (int i=0; i<10; ++i) {
			bs.add(rand.nextBoolean());
		}
		snt.setBooleanListColumn(bs);
		List<SecondLevelNestedTableInterface> snts = Lists.newArrayList();
		snts.add(snt);
		nestedTable.setNestedTableColumn(snts);

		List<FirstLevelNestedTableInterface> nts = Lists.newArrayList();
		nts.add(nestedTable);
		aup.setNestedTableColumn(nts);

		StructTableInterface sti = new StructTableRecord();
		sti.setNestedTableColumn(snts);
		aup.setStructColumn(sti);

		return aup;
	}

	@Test
	public void recordCreation() {

		MainTableInterface aup = createRecord();
		@SuppressWarnings("unchecked")
		Record<MainTableMetadata> aupRecord = (Record<MainTableMetadata>) aup.getBaseObject();
		// start testing
		int len = aupRecord.getNumOfBytes();
		int impSize = aupRecord.getListSize(MainTableMetadata.NESTED_TABLE_COLUMN);
		int idSize = aupRecord.getListSize(MainTableMetadata.INT_LIST_COLUMN);
		aupRecord.getValue(MainTableMetadata.STRUCT_COLUMN);

		Assert.assertEquals(impSize, aup.getNestedTableColumnSize());
		Assert.assertEquals(idSize, aup.getIntListColumn().size());

		GroupFieldType gft = new GroupFieldType("main_table", MainTableMetadata.class);
		Assert.assertEquals(len, FieldValueOperatorFactory.getOperator(gft).getNumOfBytes(aupRecord));
	}
	
	@Test
	public void printContent1() {
		MainTableInterface aup = new MainTableRecord();
		aup.setIntSortedColumn(101);
		aup.setLongRequiredColumn(1000L);
		String output = new MainTableInterfaceContentPrinter().apply(aup);
		System.out.println(output);
		String content = "{\r\n" + 
				"long_required_column = 1000\r\n" + 
				"int_sorted_column = 101\r\n" + 
				"byte_column = -1\r\n" + 
				"boolean_column = false\r\n" + 
				"short_column = 0\r\n" + 
				"float_column = 0.0\r\n" + 
				"double_column = 0.001\r\n" + 
				"string_column = Unknown\r\n" + 
				"binary_column = null\r\n" + 
				"nested_table_column = [\r\n" + 
				"]\r\n" + 
				"int_list_column = []\r\n" + 
				"string_derived_column = Unknown\r\n" + 
				"int_derived_column = 0\r\n" + 
				"}";
		Assert.assertEquals(output.replaceAll("\\s", ""), content.trim().replaceAll("\\s", ""));
	}
}
