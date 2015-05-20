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
package datamine.storage.recordbuffers.example.convertors;

import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.operator.UnaryOperatorInterface;
import datamine.storage.api.RecordBuilderInterface;
import java.util.*;


/**
 * DO NOT CHANGE! Auto-generated code
 */
public class AttributionResultInterfaceConvertor implements UnaryOperatorInterface<AttributionResultInterface, AttributionResultInterface> {

	private RecordBuilderInterface builder;

   public AttributionResultInterfaceConvertor(RecordBuilderInterface builder) {
   	this.builder = builder;
	}
	@Override
	public AttributionResultInterface apply(AttributionResultInterface input) {
		AttributionResultInterface output = builder.build(AttributionResultInterface.class);
		output.setId(input.getId());

		{
			List<AttributionResultRuleInterface> list = new ArrayList<AttributionResultRuleInterface>();
			AttributionResultRuleInterfaceConvertor convertor = new AttributionResultRuleInterfaceConvertor(builder);
			for (AttributionResultRuleInterface tuple: input.getRules()) {
				list.add(convertor.apply(tuple));
			}
			output.setRules(list);
		}

		return output;
	}

}

