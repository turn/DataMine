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
public interface ImpressionInterface extends BaseInterface  {

		public int getMediaProviderId();
		public short getMpTptCategoryId();
		public String getTruncatedUrl();
		public boolean isBid();
		public byte getBidType();
		public List<AttributionResultInterface> getAttributionResults();
		public long getAllowedAdFormats();
		public double getCost();
		public String getImpressionDay();

		public void setMediaProviderId(int input);
		public void setMpTptCategoryId(short input);
		public void setTruncatedUrl(String input);
		public void setBid(boolean input);
		public void setBidType(byte input);
		public void setAttributionResults(List<AttributionResultInterface> input);
		public void setAllowedAdFormats(long input);
		public void setCost(double input);

		public int getMediaProviderIdDefaultValue();
		public short getMpTptCategoryIdDefaultValue();
		public String getTruncatedUrlDefaultValue();
		public boolean getBidDefaultValue();
		public byte getBidTypeDefaultValue();
		public long getAllowedAdFormatsDefaultValue();
		public double getCostDefaultValue();
		public String getImpressionDayDefaultValue();

		public int getAttributionResultsSize();

		public void setDerivedValueImplementation(ImpressionDerivedValueInterface derived);

}

