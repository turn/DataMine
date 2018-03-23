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
 * DO Not CHANGE! Auto-generated code
 */
public class FirstLevelNestedTableTestData extends AbstractTestData<FirstLevelNestedTableInterface, FirstLevelNestedTableMetadata> {

    public FirstLevelNestedTableTestData(List<EnumMap<FirstLevelNestedTableMetadata, Object>> input) {
        super(input);
    }

    @Override
    public List<FirstLevelNestedTableInterface> createObjects(RecordBuilderInterface builder) {
		List<FirstLevelNestedTableInterface> records = Lists.newArrayList();
		for (EnumMap<FirstLevelNestedTableMetadata, Object> cur : data) {
			FirstLevelNestedTableInterface record = builder.build(FirstLevelNestedTableInterface.class);
			
			if (cur.containsKey(FirstLevelNestedTableMetadata.EVENT_TIME)) {
				record.setEventTime((Long) cur.get(FirstLevelNestedTableMetadata.EVENT_TIME));
			}

			if (cur.containsKey(FirstLevelNestedTableMetadata.INT_REQUIRED_COLUMN)) {
				record.setIntRequiredColumn((Integer) cur.get(FirstLevelNestedTableMetadata.INT_REQUIRED_COLUMN));
			}

			if (cur.containsKey(FirstLevelNestedTableMetadata.NESTED_TABLE_COLUMN)) {
				record.setNestedTableColumn(new SecondLevelNestedTableTestData((List) cur.get(FirstLevelNestedTableMetadata.NESTED_TABLE_COLUMN)).createObjects(builder));
			}

			records.add(record);
		}
		return records;
    }

    @Override
    public void assertObjects(List<FirstLevelNestedTableInterface> objectList) {
		int size = objectList.size();
		Assert.assertEquals(size, data.size());
		for (int i = 0; i < size; ++i) {
			
			if (data.get(i).containsKey(FirstLevelNestedTableMetadata.EVENT_TIME)) {
				Assert.assertEquals(objectList.get(i).getEventTime(), data.get(i).get(FirstLevelNestedTableMetadata.EVENT_TIME));
			}

			if (data.get(i).containsKey(FirstLevelNestedTableMetadata.INT_REQUIRED_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getIntRequiredColumn(), data.get(i).get(FirstLevelNestedTableMetadata.INT_REQUIRED_COLUMN));
			}

			if (data.get(i).containsKey(FirstLevelNestedTableMetadata.NESTED_TABLE_COLUMN)) {
				new SecondLevelNestedTableTestData((List) data.get(i).get(FirstLevelNestedTableMetadata.NESTED_TABLE_COLUMN)).assertObjects(objectList.get(i).getNestedTableColumn());
			}

			if (data.get(i).containsKey(FirstLevelNestedTableMetadata.STRING_DERIVED_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getStringDerivedColumn(), data.get(i).get(FirstLevelNestedTableMetadata.STRING_DERIVED_COLUMN));
			}

		}
	}

    public static List<EnumMap<FirstLevelNestedTableMetadata, Object>> createInputData(int num) {
		List<EnumMap<FirstLevelNestedTableMetadata, Object>> dataList = Lists.newArrayList();
		for (int i = 0; i < num; ++i) {
			EnumMap<FirstLevelNestedTableMetadata, Object> dataMap = Maps.newEnumMap(FirstLevelNestedTableMetadata.class);
			
			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)FirstLevelNestedTableMetadata.EVENT_TIME.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(FirstLevelNestedTableMetadata.EVENT_TIME, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)FirstLevelNestedTableMetadata.INT_REQUIRED_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(FirstLevelNestedTableMetadata.INT_REQUIRED_COLUMN, val);
				}
			}

			{
				Object val = SecondLevelNestedTableTestData.createInputData(3);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(FirstLevelNestedTableMetadata.NESTED_TABLE_COLUMN, val);
				}
			}

			if (!dataMap.isEmpty()) {
				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}


