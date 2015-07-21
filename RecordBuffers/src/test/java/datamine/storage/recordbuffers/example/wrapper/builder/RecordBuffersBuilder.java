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
package datamine.storage.recordbuffers.example.wrapper.builder;


import datamine.storage.api.BaseInterface;
import datamine.storage.api.RecordBuilderInterface;
import datamine.storage.recordbuffers.example.wrapper.*;
import datamine.storage.recordbuffers.example.interfaces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * DO NOT CHANGE! Auto-generated code
 */
public class RecordBuffersBuilder implements RecordBuilderInterface {

	static final Logger LOG = LoggerFactory.getLogger(RecordBuffersBuilder.class);

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BaseInterface> T build(Class<T> tableClass) {

		try {
			
		if (tableClass == AnalyticalUserProfileInterface.class) {
			return (T) AnalyticalUserProfileRecord.class.newInstance();
		}
		else
		if (tableClass == AttributionResultInterface.class) {
			return (T) AttributionResultRecord.class.newInstance();
		}
		else
		if (tableClass == ImpressionInterface.class) {
			return (T) ImpressionRecord.class.newInstance();
		}
		else
		if (tableClass == ProviderUserIdInterface.class) {
			return (T) ProviderUserIdRecord.class.newInstance();
		}
		else
		if (tableClass == AttributionResultRuleInterface.class) {
			return (T) AttributionResultRuleRecord.class.newInstance();
		}
		else
		if (tableClass == IdMapInterface.class) {
			return (T) IdMapRecord.class.newInstance();
		}

		} catch (InstantiationException e) {
			LOG.error("The object can not be created for " + tableClass + ":" + e);
		} catch (IllegalAccessException e) {
			LOG.error("The object can not be created for " + tableClass + ":" + e);
		}

		LOG.error("Cannot create an instance for "+tableClass);
		throw new IllegalArgumentException("Not support for the record of "+tableClass);
	}
}


