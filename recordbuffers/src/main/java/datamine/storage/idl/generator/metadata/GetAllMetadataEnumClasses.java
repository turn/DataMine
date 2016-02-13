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
package datamine.storage.idl.generator.metadata;

import java.util.Set;

import org.reflections.Reflections;

import datamine.operator.UnaryOperatorInterface;
import datamine.storage.api.RecordMetadataInterface;

/**
 * The class defines a function to collect all metadata ENUM classes under a
 * given package. 
 *  
 * @author yqi
 * @date Apr 8, 2015
 */
public class GetAllMetadataEnumClasses implements
		UnaryOperatorInterface<String, Set<Class<? extends RecordMetadataInterface>>> {

	@Override
	public Set<Class<? extends RecordMetadataInterface>> apply(String input) {
		Reflections reflections = new Reflections(input);
		Set<Class<? extends RecordMetadataInterface>> allClasses = 
		     reflections.getSubTypesOf(RecordMetadataInterface.class);
		return allClasses;
	}

}
