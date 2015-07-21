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

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.FieldTypeFactory;
import datamine.storage.idl.type.PrimitiveType;

public class DictionaryValueEncodingTest {
	
	IEncoding encoder = new DictionaryEncoding();
	
	@Test(expectedExceptions = IllegalAccessError.class) 
	public void noSupportedFloatEncoding() {
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList(23f, 23f, 0.12f, 2f, 23f, -234.42f, -234.42f)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.FLOAT), encoder);
	}
	
	@Test(expectedExceptions = IllegalAccessError.class) 
	public void noSupportedDoubleEncoding() {
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList(0.12d, 2d, 23d, -234.42d, -234.42d, -234.42d, -234.42d, -234.42d)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.DOUBLE), encoder);
	}
	
	@Test(expectedExceptions = IllegalAccessError.class) 
	public void noSupportedByteEncoding() {
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList((byte)1, (byte)23, (byte)1, (byte)1, (byte)1, (byte)1, (byte)23, (byte)1, (byte)23, (byte)44)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.BYTE), encoder);
	}
	
	@Test(expectedExceptions = IllegalAccessError.class) 
	public void noSupportedBoolEncoding() {
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList(true, false, false, false, false, false, true)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.BOOL), encoder);
	}
	@Test
	public void encoding() {
		FieldType type = FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32);
		List<Object> values1 = NoCompressionEncodingTest.getObjectList(Lists.newArrayList(1,1,2,3,3,3,4,5,6,6,6,6,6,6));
		NoCompressionEncodingTest.testEncodingFor(values1, type, encoder);
				
		values1.clear();
		for (int i=0; i<1000; i++) {
			values1.add(100);
		}
		NoCompressionEncodingTest.testEncodingFor(values1, type, encoder);
		
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList((short)1, (short)23, (short)23, (short)23, (short)44, (short)44)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.INT16), encoder);
		
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList(-234L, -234L, -234L, -234L, 23490234L, 9239234234L)), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.INT64), encoder);
				
		NoCompressionEncodingTest.testEncodingFor(
				NoCompressionEncodingTest.getObjectList(
						Lists.newArrayList("", "", "", "", "", "W@Q#J @#J$ @#$J", "W@Q#J @#J$ @#$J", "test", "234 awer0230wse af", "W@Q#J @#J$ @#$J")), 
				FieldTypeFactory.getPrimitiveType(PrimitiveType.STRING), encoder);
	}

	
	@Test
	public void estimateSize() {
		FieldType type = FieldTypeFactory.getPrimitiveType(PrimitiveType.INT32);
		
		//1. test a simple list with one element
		List<Object> values1 = NoCompressionEncodingTest.getObjectList(Lists.newArrayList(1));
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 2 + 4 + 1 * 1);
		
		//2. test a list with many repetitions
		values1 = NoCompressionEncodingTest.getObjectList(Lists.newArrayList(2,2,2,2,2));
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 2 + 4 + 5 * 1);
		
		//3. test the case where there is a large repetition
		values1.clear();
		for (int i=0; i<1000; i++) {
			values1.add(100);
		}
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 2 + 4 + 1000 * 1);
		
		//4. when it is a list of Strings]
		type = FieldTypeFactory.getPrimitiveType(PrimitiveType.STRING);
		values1 = NoCompressionEncodingTest.getObjectList(Lists.newArrayList("1111", "aaaaa"));
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 
				2 + (2+"1111".length()) + (2 + "aaaaa".length()) + 2 * 1);
		
		//5. when there are a huge list
		values1.clear();
		for (int i=0; i<1000; i++) {
			values1.add("test");
		}
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 2 + 2 + "test".length() + 1000 * 1);
		
		//6. test the type of binary
		type = FieldTypeFactory.getPrimitiveType(PrimitiveType.BINARY);
		byte[] b1 = "tawer!#!@#".getBytes();
		byte[] b2 = "ouqwer 09u	23 a f".getBytes();
		values1 = NoCompressionEncodingTest.getObjectList(Lists.newArrayList(b1, b2));
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), 
				2 + 4 + b1.length + 4 + b2.length + 2 * 1);
		
		values1 = NoCompressionEncodingTest.getObjectList(Lists.newArrayList(1f));
		type = FieldTypeFactory.getPrimitiveType(PrimitiveType.FLOAT);
		Assert.assertEquals(encoder.getEstimatedSize(values1, type, 
				new CompressStats(values1, type)), Integer.MAX_VALUE);
	}
	
}
