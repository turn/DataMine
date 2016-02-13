/**
 * Copyright (C) 2016 Turn Inc. (yan.qi@turn.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package datamine.operator;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author yqi
 * @date Sep 3, 2014
 */
public class StatisticsMerger implements
		AggregatorInterface<Map<String, Long>, Map<String, Long>> {

	Map<String, Long> result = Maps.newHashMap();
	
	@Override
	public Map<String, Long> apply(Map<String, Long> input) {
		
		for (String cur : input.keySet()) {
			if (result.containsKey(cur)) {
				result.put(cur, result.get(cur) + input.get(cur));
			} else {
				result.put(cur, input.get(cur));
			}
		}
		
		return result;
	}

	@Override
	public Map<String, Long> getResult() {
		return result;
	}

}
