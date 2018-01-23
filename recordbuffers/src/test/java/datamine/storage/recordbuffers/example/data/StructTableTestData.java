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
public class StructTableTestData extends AbstractTestData<StructTableInterface, StructTableMetadata> {

    public StructTableTestData(List<EnumMap<StructTableMetadata, Object>> input) {
        super(input);
    }

    @Override
    public List<StructTableInterface> createObjects(RecordBuilderInterface builder) {
		List<StructTableInterface> records = Lists.newArrayList();
		for (EnumMap<StructTableMetadata, Object> cur : data) {
			StructTableInterface record = builder.build(StructTableInterface.class);
			
			if (cur.containsKey(StructTableMetadata.NESTED_TABLE_COLUMN)) {
				record.setNestedTableColumn(new SecondLevelNestedTableTestData((List) cur.get(StructTableMetadata.NESTED_TABLE_COLUMN)).createObjects(builder));
			}

			if (cur.containsKey(StructTableMetadata.INT_SORTED_COLUMN)) {
				record.setIntSortedColumn((Integer) cur.get(StructTableMetadata.INT_SORTED_COLUMN));
			}

			records.add(record);
		}
		return records;
    }

    @Override
    public void assertObjects(List<StructTableInterface> objectList) {
		int size = objectList.size();
		Assert.assertEquals(size, data.size());
		for (int i = 0; i < size; ++i) {
			
			if (data.get(i).containsKey(StructTableMetadata.NESTED_TABLE_COLUMN)) {
				new SecondLevelNestedTableTestData((List) data.get(i).get(StructTableMetadata.NESTED_TABLE_COLUMN)).assertObjects(objectList.get(i).getNestedTableColumn());
			}

			if (data.get(i).containsKey(StructTableMetadata.INT_SORTED_COLUMN)) {
				Assert.assertEquals(objectList.get(i).getIntSortedColumn(), data.get(i).get(StructTableMetadata.INT_SORTED_COLUMN));
			}

		}
	}

    public static List<EnumMap<StructTableMetadata, Object>> createInputData(int num) {
		List<EnumMap<StructTableMetadata, Object>> dataList = Lists.newArrayList();
		for (int i = 0; i < num; ++i) {
			EnumMap<StructTableMetadata, Object> dataMap = Maps.newEnumMap(StructTableMetadata.class);
			
			{
				Object val = SecondLevelNestedTableTestData.createInputData(3);
				if (val != null && !((List) val).isEmpty()) {
					dataMap.put(StructTableMetadata.NESTED_TABLE_COLUMN, val);
				}
			}

			{
				Object val = RandomValueGenerator.getValueOf(((PrimitiveFieldType)StructTableMetadata.INT_SORTED_COLUMN.getField().getType()).getPrimitiveType());
				if (val != null) {
					dataMap.put(StructTableMetadata.INT_SORTED_COLUMN, val);
				}
			}

			if (!dataMap.isEmpty()) {
				dataList.add(dataMap);
			}
		}
		return dataList;
	}

}


