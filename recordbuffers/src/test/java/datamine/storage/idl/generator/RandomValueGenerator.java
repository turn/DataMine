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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;
import java.util.Random;

import datamine.storage.idl.type.PrimitiveType;

/**
 * Generate a random value based on the input type. 
 * 
 * <p>
 * It works for the primitive type field only. 
 * </p>
 * 
 * @author yqi
 * @date Feb 20, 2015
 */
public class RandomValueGenerator {
	
	final static Random rand1 = new Random();
	final static RandomStringUtils rand2 = new RandomStringUtils();
	
	public static Object getValueOf(PrimitiveType type) {
		
		switch (type) {
		case BOOL:
			return rand1.nextBoolean();
		case BYTE:
			return (byte) rand1.nextInt(Byte.MAX_VALUE);
		case INT16:
			return (short) rand1.nextInt(Short.MAX_VALUE);
		case INT32:
			return rand1.nextInt(Integer.MAX_VALUE);
		case INT64:
			return rand1.nextLong();
		case FLOAT:
			return rand1.nextFloat();
		case DOUBLE:
			return rand1.nextDouble();
		case STRING:
			return "abasf_"+System.currentTimeMillis();//rand2.random(256);
		case BINARY:
			byte[] ret = new byte[1000];
			rand1.nextBytes(ret);
			return ret;
		case UNKNOWN:
			return null;
		default:
			throw new IllegalArgumentException("Not support the type: "+type);
		}
	}
	
	public static List<Object> getValueArrayOf(PrimitiveType type, int num) {
		Preconditions.checkArgument(num > 0);
		
		if (type.equals(PrimitiveType.UNKNOWN)) {
			return null;
		}
		
		List<Object> ret = Lists.newArrayList();		
		for (int i = 0; i < num; ++i) {
			ret.add(getValueOf(type));
		}
		return ret;
	}
}