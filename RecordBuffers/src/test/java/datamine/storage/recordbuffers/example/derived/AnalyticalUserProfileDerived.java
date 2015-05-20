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
package datamine.storage.recordbuffers.example.derived;

import datamine.storage.recordbuffers.example.interfaces.AnalyticalUserProfileDerivedValueInterface;
import datamine.storage.recordbuffers.example.interfaces.AnalyticalUserProfileInterface;

/**
 * @author yqi
 * @date May 20, 2015
 */
public class AnalyticalUserProfileDerived implements
		AnalyticalUserProfileDerivedValueInterface {

	private final AnalyticalUserProfileInterface aup;
	
	public AnalyticalUserProfileDerived(AnalyticalUserProfileInterface aup) {
		this.aup = aup;
	}
	
	@Override
	public String getDay() {
		return "Monday";
	}

}
