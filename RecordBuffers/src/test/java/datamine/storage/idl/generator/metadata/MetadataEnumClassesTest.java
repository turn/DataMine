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

package datamine.storage.idl.generator.metadata;

import java.util.Set;

import junit.framework.Assert;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.recordbuffers.RecordBufferMeta;

public class MetadataEnumClassesTest {
	
	private static final String packageName =
			"datamine.storage.recordbuffers.example.model";
	private Set<Class<? extends RecordMetadataInterface>> enumSet = null;
	@BeforeClass
	private void prepare() {
		GetAllMetadataEnumClasses game = new GetAllMetadataEnumClasses();
		enumSet = game.apply(packageName);
	}
	
	@Test
	public void getRecordMetadata() {
		String tableName = "analytical_user_profile";
		@SuppressWarnings("rawtypes")
		RecordBufferMeta rbm = new GetRecordBufferMeta().apply(packageName, tableName);
		Assert.assertEquals(tableName, rbm.getTableName());
	}
	
	@Test
	public void getAllMetadataEnumClasses() {
		Assert.assertEquals(6, enumSet.size());
	}
	
}
