package datamine.storage.recordbuffers.example.interfaces;

import datamine.storage.api.BaseInterface;

import java.util.List;


/**
 * DO Not CHANGE! Auto-generated code
 */
public interface MainTableInterface extends BaseInterface , Comparable<MainTableInterface> {

		public long getLongRequiredColumn();
		public int getIntSortedColumn();
		public byte getByteColumn();
		public boolean isBooleanColumn();
		public short getShortColumn();
		public float getFloatColumn();
		public double getDoubleColumn();
		public String getStringColumn();
		public byte[] getBinaryColumn();
		public List<FirstLevelNestedTableInterface> getNestedTableColumn();
		public StructTableInterface getStructColumn();
		public List<Integer> getIntListColumn();
		public String getStringDerivedColumn();
		public int getIntDerivedColumn();

		public void setLongRequiredColumn(long input);
		public void setIntSortedColumn(int input);
		public void setByteColumn(byte input);
		public void setBooleanColumn(boolean input);
		public void setShortColumn(short input);
		public void setFloatColumn(float input);
		public void setDoubleColumn(double input);
		public void setStringColumn(String input);
		public void setBinaryColumn(byte[] input);
		public void setNestedTableColumn(List<FirstLevelNestedTableInterface> input);
		public void setStructColumn(StructTableInterface input);
		public void setIntListColumn(List<Integer> input);

		public long getLongRequiredColumnDefaultValue();
		public int getIntSortedColumnDefaultValue();
		public byte getByteColumnDefaultValue();
		public boolean getBooleanColumnDefaultValue();
		public short getShortColumnDefaultValue();
		public float getFloatColumnDefaultValue();
		public double getDoubleColumnDefaultValue();
		public String getStringColumnDefaultValue();
		public String getStringDerivedColumnDefaultValue();
		public int getIntDerivedColumnDefaultValue();

		public int getNestedTableColumnSize();
		public int getIntListColumnSize();

		public void setDerivedValueImplementation(MainTableDerivedValueInterface derived);

}

