package datamine.storage.recordbuffers.example.interfaces;

import datamine.storage.api.BaseInterface;

import java.util.List;


/**
 * DO Not CHANGE! Auto-generated code
 */
public interface SecondLevelNestedTableInterface extends BaseInterface  {

		public byte getByteRequiredColumn();
		public List<Boolean> getBooleanListColumn();

		public void setByteRequiredColumn(byte input);
		public void setBooleanListColumn(List<Boolean> input);

		public byte getByteRequiredColumnDefaultValue();

		public int getBooleanListColumnSize();


}

