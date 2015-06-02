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

import java.nio.ByteBuffer;

/**
 * The interface defines the behaviors of a value operator. 
 * 
 * @author yqi
 */
public interface FieldValueOperatorInterface {
	/**
	 * Check if the input is valid
	 * @param val the input object
	 * @return true if it is a valid value considering the type it represents
	 */
	public boolean isValid(Object val);
	
	/**
	 * Check if it is a type whose value has a fixing memory usage
	 * @return true if the type has its value use the fixing memory size
	 */
	public boolean hasFixedLength();
	
	/**
	 * Convert the input object into an array of bytes
	 * @param val the input object
	 * @return an array of bytes to represent the input object
	 */
	public byte[] getByteArray(Object val);
	
	/**
	 * Get the value from an array of bytes
	 * @param buffer the input byte buffer containing the value
	 * @param index the index where the value gets started to store
	 * @param length the number of bytes of the concerned value taking
	 * @return
	 */
	public Object getValue(ByteBuffer buffer, int index, int length);
	
	/**
	 * The following getters define the approach to get the primitive as it is
	 */
	public boolean getBool(ByteBuffer buffer, int index);
	public byte getByte(ByteBuffer buffer, int index);
	public short getShort(ByteBuffer buffer, int index);
	public long getLong(ByteBuffer buffer, int index);
	public int getInt(ByteBuffer buffer, int index);
	public float getFloat(ByteBuffer buffer, int index);
	public double getDouble(ByteBuffer buffer, int index);
	public byte[] getBinary(ByteBuffer buffer, int index, int length);
	public String getString(ByteBuffer buffer, int index, int length);
	
	/**
	 * Get the number of bytes used by the input
	 * @param val the input object
	 * @return the number of bytes used by the input
	 */
	public int getNumOfBytes(Object val);
	
	/**
	 * Define the number of bytes to record the metadata when serializing the 
	 * value of type
	 * @return the number of bytes used for the metadata when serializing the type of interested
	 */
	public int getMetadataLength();
}
