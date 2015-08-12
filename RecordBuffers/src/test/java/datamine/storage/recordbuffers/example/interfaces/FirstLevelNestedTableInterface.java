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
public interface FirstLevelNestedTableInterface extends BaseInterface  {

		public int getIntRequiredColumn();
		public List<SecondLevelNestedTableInterface> getNestedTableColumn();
		public String getStringDerivedColumn();

		public void setIntRequiredColumn(int input);
		public void setNestedTableColumn(List<SecondLevelNestedTableInterface> input);

		public int getIntRequiredColumnDefaultValue();
		public String getStringDerivedColumnDefaultValue();

		public int getNestedTableColumnSize();

		public void setDerivedValueImplementation(FirstLevelNestedTableDerivedValueInterface derived);

}

