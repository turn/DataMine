package datamine.storage.recordbuffers.example.interfaces;

import datamine.storage.api.BaseInterface;

import java.util.List;


/**
 * DO Not CHANGE! Auto-generated code
 */
public interface StructTableInterface extends BaseInterface  {

		public List<SecondLevelNestedTableInterface> getNestedTableColumn();
		public int getIntSortedColumn();

		public void setNestedTableColumn(List<SecondLevelNestedTableInterface> input);
		public void setIntSortedColumn(int input);

		public int getIntSortedColumnDefaultValue();

		public int getNestedTableColumnSize();


}

