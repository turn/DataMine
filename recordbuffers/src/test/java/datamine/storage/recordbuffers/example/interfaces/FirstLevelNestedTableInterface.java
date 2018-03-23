package datamine.storage.recordbuffers.example.interfaces;

import datamine.storage.api.BaseInterface;

import java.util.List;


/**
 * DO Not CHANGE! Auto-generated code
 */
public interface FirstLevelNestedTableInterface extends BaseInterface , Comparable<FirstLevelNestedTableInterface> {

		public long getEventTime();
		public int getIntRequiredColumn();
		public List<SecondLevelNestedTableInterface> getNestedTableColumn();
		public String getStringDerivedColumn();

		public void setEventTime(long input);
		public void setIntRequiredColumn(int input);
		public void setNestedTableColumn(List<SecondLevelNestedTableInterface> input);

		public long getEventTimeDefaultValue();
		public int getIntRequiredColumnDefaultValue();
		public String getStringDerivedColumnDefaultValue();

		public int getNestedTableColumnSize();

		public void setDerivedValueImplementation(FirstLevelNestedTableDerivedValueInterface derived);

}

