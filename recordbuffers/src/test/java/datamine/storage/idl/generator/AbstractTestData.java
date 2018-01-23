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
package datamine.storage.idl.generator;

import java.util.EnumMap;
import java.util.List;

import datamine.storage.api.BaseInterface;
import datamine.storage.api.RecordMetadataInterface;


/**
 * @author yqi
 * @date Feb 18, 2015
 */
abstract public class AbstractTestData<T extends BaseInterface, S extends Enum<S> & RecordMetadataInterface>
	implements GenerateTestDataInterface<T> {
	
	protected final List<EnumMap<S, Object>> data;
	
	public AbstractTestData(List<EnumMap<S, Object>> input) {
		this.data = input;
	}
}
