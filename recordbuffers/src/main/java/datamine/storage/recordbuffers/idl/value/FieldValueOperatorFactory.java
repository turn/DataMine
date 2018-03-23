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
package datamine.storage.recordbuffers.idl.value;

import com.google.common.collect.Maps;

import java.util.Map;

import datamine.storage.idl.FieldValueOperatorInterface;
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.idl.type.PrimitiveFieldType;

/**
 * @author yqi
 * @date Mar 31, 2015
 */
public class FieldValueOperatorFactory {
	
	private FieldValueOperatorFactory() {  }
	
	private static Map<FieldType, FieldValueOperatorInterface> valueOprMap = Maps.newHashMap();
	
	public static final FieldValueOperatorInterface getOperator(FieldType type) {
		if (valueOprMap.containsKey(type)) {
			return valueOprMap.get(type);
		} else {
			FieldValueOperatorInterface newOpr = null;
			if (type instanceof PrimitiveFieldType) {
				newOpr = new PrimitiveValueOperator((PrimitiveFieldType) type);
			} else if (type instanceof GroupFieldType) {
				newOpr = new GroupValueOperator((GroupFieldType) type);
			} else if (type instanceof CollectionFieldType) {
				newOpr = new CollectionValueOperator((CollectionFieldType) type);
			} else {
				throw new IllegalArgumentException("Not a valid type - " + type);
			}	
			valueOprMap.put(type, newOpr);
			return newOpr;
		}
	}
}
