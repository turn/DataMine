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
package datamine.storage.recordbuffers.pax.encoding;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.FieldTypeFactory;
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.idl.type.PrimitiveType;

public class NoCompressionEncodingTest {
	
	IEncoding encoder = new NoCompressionEncoding();
	
	@Test
	public void encoding() {
		FieldType type = FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32);
		List<Object> values1 = getObjectList(Lists.newArrayList(1,1,2,3,3,3,4,5,6,6,6,6,6,6));
		testEncodingFor(values1, type, encoder);
				
		values1.clear();
		for (int i=0; i<1000; i++) {
			values1.add(100);
		}
		testEncodingFor(values1, type, encoder);
		
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList((short)1, (short)23, (short)44)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.INT16), encoder);
		
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList(-234L, 23490234L, 9239234234L)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.INT64), encoder);
		
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList(0.12f, 2f, 23f, -234.42f)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.FLOAT), encoder);
		
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList(0.12d, 2d, 23d, -234.42d)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.DOUBLE), encoder);
		
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList((byte)1, (byte)23, (byte)44)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.BYTE), encoder);
		
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList(true, false, false)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.BOOL), encoder);
		
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList("", "test", "234 awer0230wse af", "W@Q#J @#J$ @#$J")), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.STRING), encoder);
	}
	
	
	@Test
	public void estimateSize() {
		FieldType type = FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32);
		
		//1. test a simple list with one element
		List<Object> values1 = getObjectList(Lists.newArrayList(1));
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 4);
		
		//2. test a list with many repetitions
		values1 = getObjectList(Lists.newArrayList(2,2,2,2,2));
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 4 * values1.size());
		
		//3. test the case where there is a large repetition
		values1.clear();
		for (int i=0; i<1000; i++) {
			values1.add(100);
		}
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 4 * 1000);
		
		//4. when it is a list of Strings]
		type = FieldTypeFactory.getPrimitiveType(PrimitiveType.STRING);
		values1 = getObjectList(Lists.newArrayList("1111", "aaaaa"));
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 
				2 + "1111".length() + 2 + "aaaaa".length());
		
		//5. when there are a huge list
		values1.clear();
		for (int i=0; i<1000; i++) {
			values1.add("test");
		}
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 1000 * (2+"test".length()));
		
		//6. test the type of binary
		type = FieldTypeFactory.getPrimitiveType(PrimitiveType.BINARY);
		byte[] b1 = "tawer!#!@#".getBytes();
		byte[] b2 = "ouqwer 09u	23 a f".getBytes();
		values1 = getObjectList(Lists.newArrayList(b1, b2));
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 
				4 + b1.length + 4 + b2.length);
	}
	
	
	/////////////////////////////////////////////////////////////////////////
	// Below are the static methods shared by all tests 
	/////////////////////////////////////////////////////////////////////////
	
	public static <T> List<Object> getObjectList(List<T> list) {
		List<Object> ret = Lists.newArrayList();
		ret.addAll(list);
		return ret;
	}
	
	public static void testEncodingFor(List<Object> input, FieldType type, IEncoding encoder) {
		
		byte[] bytes = encoder.getBytes(input, type, 
				new CompressStats(input, type));
		
		ByteBuffer buf = ByteBuffer.wrap(bytes); 
		
		List<Object> values2 = encoder.getValues(buf, 0, type, input.size());
		Assert.assertEquals(input, values2);
		
		// test the primitive values
		if (type instanceof PrimitiveFieldType) {
			switch (((PrimitiveFieldType) type).getPrimitiveType()) {
			case INT32:
				Integer[] ints = ArrayUtils.toObject(encoder.getIntValues(buf, 0, input.size())); 
				Assert.assertEquals(Arrays.asList(ints), input);
				break;
			case INT16:
				Short[] shorts = ArrayUtils.toObject(encoder.getShortValues(buf, 0, input.size())); 
				Assert.assertEquals(Arrays.asList(shorts), input);
				break;
			case INT64:
				Long[] longs = ArrayUtils.toObject(encoder.getLongValues(buf, 0, input.size())); 
				Assert.assertEquals(Arrays.asList(longs), input);
				break;
			case FLOAT:
				Float[] floats = ArrayUtils.toObject(encoder.getFloatValues(buf, 0, input.size())); 
				Assert.assertEquals(Arrays.asList(floats), input);
				break;
			case DOUBLE:
				Double[] doubles = ArrayUtils.toObject(encoder.getDoubleValues(buf, 0, input.size())); 
				Assert.assertEquals(Arrays.asList(doubles), input);
				break;
			case BYTE:
				Byte[] bytess = ArrayUtils.toObject(encoder.getByteValues(buf, 0, input.size())); 
				Assert.assertEquals(Arrays.asList(bytess), input);
				break;
			case BOOL:
				Boolean[] bools = ArrayUtils.toObject(encoder.getBoolValues(buf, 0, input.size())); 
				Assert.assertEquals(Arrays.asList(bools), input);
				break;
			case STRING:
				String[] texts = encoder.getStringValues(buf, 0, input.size()); 
				Assert.assertEquals(Arrays.asList(texts), input);
				break;
			case BINARY:
				
				break;
			default:
				throw new RuntimeException("Doesn't support the type: " + type);
			}			
		}
	}
	
}
