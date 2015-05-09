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
public class ImpressionInterfaceConvertor implements UnaryOperatorInterface<ImpressionInterface, ImpressionInterface> {

	private RecordBuilderInterface builder;

   public ImpressionInterfaceConvertor(RecordBuilderInterface builder) {
   	this.builder = builder;
	}
	@Override
	public ImpressionInterface apply(ImpressionInterface input) {
		ImpressionInterface output = builder.build(ImpressionInterface.class);
		output.setMediaProviderId(input.getMediaProviderId());

		output.setMpTptCategoryId(input.getMpTptCategoryId());

		output.setTruncatedUrl(input.getTruncatedUrl());

		output.setBid(input.isBid());

		output.setBidType(input.getBidType());

		{
			List<AttributionResultInterface> list = new ArrayList<AttributionResultInterface>();
			AttributionResultInterfaceConvertor convertor = new AttributionResultInterfaceConvertor(builder);
			for (AttributionResultInterface tuple: input.getAttributionResults()) {
				list.add(convertor.apply(tuple));
			}
			output.setAttributionResults(list);
		}
		output.setAllowedAdFormats(input.getAllowedAdFormats());

		output.setCost(input.getCost());


		return output;
	}

}

