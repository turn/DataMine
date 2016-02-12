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
package datamine.storage.recordbuffers.example.interfaces;

import datamine.storage.api.BaseInterface;

import java.util.List;


/**
 * DO NOT CHANGE! Auto-generated code
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

