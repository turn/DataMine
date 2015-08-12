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
public class MainTableTestData extends AbstractTestData<MainTableInterface, MainTableMetadata> {

    public MainTableTestData(List<EnumMap<MainTableMetadata, Object>> input) {
        super(input);
    }

    @Override
    public List<MainTableInterface> createObjects(RecordBuilderInterface builder) {
		List<MainTableInterface> records = Lists.newArrayList();
		for (EnumMap<MainTableMetadata, Object> cur : data) {
			MainTableInterface record = builder.build(MainTableInterface.class);
			
			if (cur.containsKey(MainTableMetadata.LONG_REQUIRED_COLUMN)) {
				record.setLongRequiredColumn((Long) cur.get(MainTableMetadata.LONG_REQUIRED_COLUMN));
			}

			if (cur.containsKey(MainTableMetadata.INT_SORTED_COLUMN)) {
				record.setIntSortedColumn((Integer) cur.get(MainTableMetadata.INT_SORTED_COLUMN));
			}

			if (cur.containsKey(MainTableMetadata.BYTE_COLUMN)) {
				record.setByteColumn((Byte) cur.get(MainTableMetadata.BYTE_COLUMN));
			}

			if (cur.containsKey(MainTableMetadata.BOOLEAN_COLUMN)) {
				record.setBooleanColumn((Boolean) cur.get(MainTableMetadata.BOOLEAN_COLUMN));
			}

			if (cur.containsKey(MainTableMetadata.SHORT_COLUMN)) {
				record.setShortColumn((Short) cur.get(MainTableMetadata.SHORT_COLUMN));
			}

			if (cur.containsKey(MainTableMetadata.FLOAT_COLUMN)) {
				record.setFloatColumn((Float) cur.get(MainTableMetadata.FLOAT_COLUMN));
			}

			if (cur.containsKey(MainTableMetadata.DOUBLE_COLUMN)) {
				record.setDoubleColumn((Double) cur.get(MainTableMetadata.DOUBLE_COLUMN));
			}

			if (cur.containsKey(MainTableMetadata.STRING_COLUMN)) {
				record.setStringColumn((String) cur.get(MainTableMetadata.STRING_COLUMN));
			}

			if (cur.containsKey(MainTableMetadata.BINARY_COLUMN)) {
				record.setBinaryColumn((byte[]) cur.get(MainTableMetadata.BINARY_COLUMN));
			}

			if (cur.containsKey(MainTableMetadata.NESTED_TABLE_COLUMN)) {
				record.setNestedTableColumn(new FirstLevelNestedTableTestData((List) cur.get(MainTableMetadata.NESTED_TABLE_COLUMN)).createObjects(builder));
			}

			if (cur.containsKey(MainTableMetadata.STRUCT_COLUMN)) {
				record.setStructColumn(new StructTableTestData((List) cur.get(MainTableMetadata.STRUCT_COLUMN)).createObjects(builder).get(0));
			}

			if (cur.containsKey(MainTableMetadata.INT_LIST_COLUMN)) {
				record.setIntListColumn((List) cur.get(MainTableMetadata.INT_LIST_COLUMN));
			}

			records.add(record);
		}
		return records;
    }

    @Override
    public void assertObjects(List<MainTableInterface> objectList) {
		int size = objectList.size();
		Assert.assertEquals(size, data.size());
		for (int i = 0; i < size; ++i) {
			
			if (data.get(i).containsKey(MainTableMetadata.LONG_REQUIRED_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getLongRequiredColumn(), data.get(i).get(MainTableMetadata.LONG_REQUIRED_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.INT_SORTED_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getIntSortedColumn(), data.get(i).get(MainTableMetadata.INT_SORTED_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.BYTE_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getByteColumn(), data.get(i).get(MainTableMetadata.BYTE_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.BOOLEAN_COLUMN)) {
				Assert.assertEquals(objectList.get(i).isBooleanColumn(), data.get(i).get(MainTableMetadata.BOOLEAN_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.SHORT_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getShortColumn(), data.get(i).get(MainTableMetadata.SHORT_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.FLOAT_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getFloatColumn(), data.get(i).get(MainTableMetadata.FLOAT_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.DOUBLE_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getDoubleColumn(), data.get(i).get(MainTableMetadata.DOUBLE_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.STRING_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getStringColumn(), data.get(i).get(MainTableMetadata.STRING_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.BINARY_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getBinaryColumn(), data.get(i).get(MainTableMetadata.BINARY_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.NESTED_TABLE_COLUMN)) {
				new FirstLevelNestedTableTestData((List) data.get(i).get(MainTableMetadata.NESTED_TABLE_COLUMN)).assertObjects(objectList.get(i).getNestedTableColumn());
			}

			if (data.get(i).containsKey(MainTableMetadata.STRUCT_COLUMN)) {
				new StructTableTestData((List) data.get(i).get(MainTableMetadata.STRUCT_COLUMN)).assertObjects(Lists.newArrayList(objectList.get(i).getStructColumn()));
			}

			if (data.get(i).containsKey(MainTableMetadata.INT_LIST_COLUMN)) {
				Assert.assertEquals((List) objectList.get(i).getIntListColumn(), data.get(i).get(MainTableMetadata.INT_LIST_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.STRING_DERIVED_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getStringDerivedColumn(), data.get(i).get(MainTableMetadata.STRING_DERIVED_COLUMN));
			}

			if (data.get(i).containsKey(MainTableMetadata.INT_DERIVED_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getIntDerivedColumn(), data.get(i).get(MainTableMetadata.INT_DERIVED_COLUMN));
			}

		}
	}

    public static List<EnumMap<MainTableMetadata, Object>> createInputData(int num) {
		List<EnumMap<MainTableMetadata, Object>> dataList = Lists.newArrayList();
		for (int i = 0; i < num; ++i) {
			EnumMap<MainTableMetadata, Object> dataMap = Maps.newEnumMap(MainTableMetadata.class);
			
			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)MainTableMetadata.LONG_REQUIRED_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(MainTableMetadata.LONG_REQUIRED_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)MainTableMetadata.INT_SORTED_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(MainTableMetadata.INT_SORTED_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)MainTableMetadata.BYTE_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(MainTableMetadata.BYTE_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)MainTableMetadata.BOOLEAN_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(MainTableMetadata.BOOLEAN_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)MainTableMetadata.SHORT_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(MainTableMetadata.SHORT_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)MainTableMetadata.FLOAT_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(MainTableMetadata.FLOAT_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)MainTableMetadata.DOUBLE_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(MainTableMetadata.DOUBLE_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)MainTableMetadata.STRING_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(MainTableMetadata.STRING_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)MainTableMetadata.BINARY_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(MainTableMetadata.BINARY_COLUMN, val);
				}
			}

			{
				Object val = FirstLevelNestedTableTestData.createInputData(3);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(MainTableMetadata.NESTED_TABLE_COLUMN, val);
				}
			}

			{
				Object val = StructTableTestData.createInputData(1);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(MainTableMetadata.STRUCT_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueArrayOf(((PrimitiveFieldType) ((CollectionFieldType)MainTableMetadata.INT_LIST_COLUMN.getField().getType()).getElementType()).getPrimitiveType(), num);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(MainTableMetadata.INT_LIST_COLUMN, val);
				}
			}

			if (!dataMap.isEmpty()) {
				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}


