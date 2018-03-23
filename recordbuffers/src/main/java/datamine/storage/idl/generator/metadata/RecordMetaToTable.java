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

import java.util.Arrays;
import java.util.List;

import datamine.operator.UnaryOperatorInterface;
import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Field;
import datamine.storage.idl.Table;

/**
 * The class creates a table schema from an ENUM class for the table metadata.
 * 
 * @author yqi
 * @date Apr 8, 2015
 */
public class RecordMetaToTable<T extends Enum<T> & RecordMetadataInterface> implements
		UnaryOperatorInterface<Class<T>, Table> {

	@Override
	public Table apply(Class<T> clazz) {

		List<T> fieldList = Arrays.asList(clazz.getEnumConstants());
		
		if (fieldList.isEmpty()) {
			return null;
		}
		String tableName = fieldList.get(0).getTableName();
		short version = fieldList.get(0).getVersion();
		List<Field> tableFields = Lists.newArrayList();
		for (T cur : fieldList) {
			tableFields.add(cur.getField());
		}
		
		return new Table(tableName, tableFields, version);
	}

}
