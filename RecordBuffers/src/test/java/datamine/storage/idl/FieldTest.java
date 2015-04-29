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
package datamine.storage.idl;

import java.util.EnumSet;

import org.testng.Assert;
import org.testng.annotations.Test;

import datamine.storage.idl.Field;

public class FieldTest {
	
	@Test
	public void getContraintEnumSet() {
		
		boolean isRequired = false;
		boolean isSorted = false;
		boolean isAscSorted = true;
		boolean hasRef = false;
		
		EnumSet<Field.Constraint> constraints = Field.getContraintEnumSet(
				isRequired, isSorted, isAscSorted, hasRef);
		Assert.assertEquals(constraints, EnumSet.of(Field.Constraint.OPTIONAL));
		
		isRequired = true;
		isSorted = false;
		isAscSorted = true;
		
		constraints = Field.getContraintEnumSet(
				isRequired, isSorted, isAscSorted, hasRef);
		Assert.assertEquals(constraints, EnumSet.of(Field.Constraint.REQUIRED));
		
		isRequired = true;
		isSorted = true;
		isAscSorted = false;
		
		constraints = Field.getContraintEnumSet(
				isRequired, isSorted, isAscSorted, hasRef);
		Assert.assertEquals(constraints, EnumSet.of(Field.Constraint.REQUIRED, 
				Field.Constraint.DES_SORTED));
	}
}
