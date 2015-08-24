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

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.EnumSet;

public class FieldTest {

    @Test
    public void getContraintEnumSet() {

        boolean isRequired = false;
        boolean isDesSorted = false;
        boolean isAscSorted = false;
        boolean isFrequentlyUsed = false;
        boolean isDerived = false;
        boolean hasLargeList = false;

        EnumSet<Field.Constraint> constraints = Field.getContraintEnumSet(
                isRequired, isDesSorted, isAscSorted, isFrequentlyUsed,
                isDerived, hasLargeList);
        Assert.assertEquals(constraints, EnumSet.of(Field.Constraint.OPTIONAL));

        isRequired = true;
        isDesSorted = false;
        isAscSorted = false;

        constraints = Field.getContraintEnumSet(
                isRequired, isDesSorted, isAscSorted, isFrequentlyUsed,
                isDerived, hasLargeList);
        Assert.assertEquals(constraints, EnumSet.of(Field.Constraint.REQUIRED));

        isRequired = true;
        isDesSorted = true;
        isAscSorted = false;

        constraints = Field.getContraintEnumSet(
                isRequired, isDesSorted, isAscSorted, isFrequentlyUsed,
                isDerived, hasLargeList);
        Assert.assertEquals(constraints, EnumSet.of(Field.Constraint.REQUIRED,
                Field.Constraint.DES_SORTED));
    }

    @Test(expectedExceptions = java.lang.IllegalArgumentException.class)
    void checkDerived() {
        boolean isDesSorted = true;
        boolean isAscSorted = true;
        Field.getContraintEnumSet(false, isDesSorted, isAscSorted, false,
                false, false);
    }
}
