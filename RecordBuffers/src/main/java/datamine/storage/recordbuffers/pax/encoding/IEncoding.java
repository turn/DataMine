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
import java.util.List;

import datamine.storage.idl.type.FieldType;

/**
 * The interface defines methods for encoding
 * @author yqi
 */
public interface IEncoding {
	
	static final int MAX_SHORT = 32768;
	static final int MAX_BYTE = 128;
	

	/**
	 * Have an estimation of the data size to store the input objects
	 * 
	 * @param values a list of objects to store (NOTE: no Nulls are allowed)
	 * @param type the field type of input
	 * @param stats the statistics information about the input list
	 * @return an estimation of the data size to store the input objects
	 */
	int getEstimatedSize(List<Object> values, FieldType type, CompressStats stats);
	
	/**
	 * Generate a byte array to store a list of input values
	 * 
	 * @param values a list of objects as input (NOTE: no Nulls are allowed)
	 * @param type the field type of input
	 * @param stats the statistics information about the input list
	 * @return a byte array to store a list of input values
	 */
	byte[] getBytes(List<Object> values, FieldType type, CompressStats stats);
	
	
	/**
	 * Create a list of objects from the input byte buffer 
	 * 
	 * @param buffer the byte buffer where a list of values are stored
	 * @param offset the offset of the content in the byte buffer
	 * @param type the field type of the output list
	 * @param size the number of values in the output
	 * @return a list of objects from the input byte buffer
	 */
	List<Object> getValues(ByteBuffer buffer, int offset, FieldType type, int size);
	
	
	/**
	 * Create an array of primitive values from the input byte buffer
	 * 
	 * @param buffer the byte buffer where the output is stored
	 * @param offset the offset where the content gets started in the byte buffer
	 * @param size the number of objects in the output
	 * @return an array of primitive values from the input byte buffer
	 */
	int[] getIntValues(ByteBuffer buffer, int offset, int size);
	short[] getShortValues(ByteBuffer buffer, int offset, int size);
	long[] getLongValues(ByteBuffer buffer, int offset, int size);
	byte[] getByteValues(ByteBuffer buffer, int offset, int size);
	boolean[] getBoolValues(ByteBuffer buffer, int offset, int size);
	float[] getFloatValues(ByteBuffer buffer, int offset, int size);
	double[] getDoubleValues(ByteBuffer buffer, int offset, int size);
	String[] getStringValues(ByteBuffer buffer, int offset, int size);
	byte[][] getBinaryValues(ByteBuffer buffer, int offset, int size);
}
