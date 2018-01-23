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

import datamine.operator.BinaryOperatorInterface;
import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.recordbuffers.RecordBufferMeta;

/**
 * The class defines a function to find the corresponding {@link RecordBufferMeta}
 * given the table name under a given package. 
 *  
 * @author yqi
 */
public class GetRecordBufferMeta<T extends Enum<T> & RecordMetadataInterface> implements
    BinaryOperatorInterface<String, String, RecordBufferMeta<T>> {

	@SuppressWarnings("unchecked")
	@Override
	public RecordBufferMeta<T> apply(String packageName, String tableName) {
		
		Set<Class<? extends RecordMetadataInterface>> allClasses =
		     new GetAllMetadataEnumClasses().apply(packageName);
		
		for (@SuppressWarnings("rawtypes") Class cur : allClasses) {
			if (RecordBufferMeta.getRecordOperator(cur).getTableName().equals(tableName)) {
				return RecordBufferMeta.getRecordOperator(cur);
			}
		}
		return null;
	}

}
