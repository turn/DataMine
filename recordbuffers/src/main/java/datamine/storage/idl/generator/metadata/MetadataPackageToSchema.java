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

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

import datamine.operator.UnaryOperatorInterface;
import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Schema;
import datamine.storage.idl.Table;

/**
 * The class defines a function to create a schema from a set of ENUM classes 
 * given a package name.
 * 
 * @author yqi
 */
public class MetadataPackageToSchema implements
		UnaryOperatorInterface<String, Schema> {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Schema apply(String packageName) {
		//1. the input string must be a valid package name
		Set<Class<? extends RecordMetadataInterface>> allEnums =
				new GetAllMetadataEnumClasses().apply(packageName);
		
		if (allEnums == null || allEnums.isEmpty()) {
			return null;
		}
		
		//2. collect all ENUM classes under the input package,
		//   and create their table schema
		List<Table> tables = Lists.newArrayList();
		for (Class<? extends RecordMetadataInterface> cur : allEnums) {
			tables.add(new RecordMetaToTable().apply(cur));
		}
		
		//3. generate the output
		return new Schema(packageName, tables);
	}
}
