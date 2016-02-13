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
package datamine.storage.api;


/**
 * The interface defines the behaviors of a record builder.
 * 
 * @author yqi
 * @date Nov 3, 2014
 */
public interface RecordBuilderInterface {
	
	public static final String RECORD_BUILDER_CLASS_PKEY = "datamine.storage.record.builder.class";
	public static final String RECORD_INTERFACE_CLASS_PKEY = "datamine.storage.record.inteface.class";
	
	/**
	 * Create a record with the input structure
	 * @param recordClass
	 * @return
	 */
	public <T extends BaseInterface> T build(Class<T> recordClass); 
}
