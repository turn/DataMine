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
public class ProviderUserIdInterfaceConvertor implements UnaryOperatorInterface<ProviderUserIdInterface, ProviderUserIdInterface> {

	private RecordBuilderInterface builder;

   public ProviderUserIdInterfaceConvertor(RecordBuilderInterface builder) {
   	this.builder = builder;
	}
	@Override
	public ProviderUserIdInterface apply(ProviderUserIdInterface input) {
		ProviderUserIdInterface output = builder.build(ProviderUserIdInterface.class);
		output.setProviderType(input.getProviderType());

		output.setProviderId(input.getProviderId());


		return output;
	}

}

