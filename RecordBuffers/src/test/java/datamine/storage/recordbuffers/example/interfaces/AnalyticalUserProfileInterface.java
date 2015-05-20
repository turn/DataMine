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
public interface AnalyticalUserProfileInterface extends BaseInterface , Comparable<AnalyticalUserProfileInterface> {

		public long getUserId();
		public byte getVersion();
		public short getResolution();
		public String getOsVersion();
		public List<ImpressionInterface> getImpressions();
		public IdMapInterface getIdMaps();
		public List<Integer> getTimeList();
		public String getDay();

		public void setUserId(long input);
		public void setVersion(byte input);
		public void setResolution(short input);
		public void setOsVersion(String input);
		public void setImpressions(List<ImpressionInterface> input);
		public void setIdMaps(IdMapInterface input);
		public void setTimeList(List<Integer> input);

		public long getUserIdDefaultValue();
		public byte getVersionDefaultValue();
		public short getResolutionDefaultValue();
		public String getOsVersionDefaultValue();
		public String getDayDefaultValue();

		public int getImpressionsSize();
		public int getTimeListSize();

		public void setDerivedValueImplementation(AnalyticalUserProfileDerivedValueInterface derived);

}

