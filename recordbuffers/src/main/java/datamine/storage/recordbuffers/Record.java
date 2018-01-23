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
package datamine.storage.recordbuffers;

import com.google.common.base.Preconditions;

import java.io.Serializable;

import datamine.storage.api.RecordMetadataInterface;
import datamine.storage.idl.Field;

/**
 * The definition of record for each tuple stored in the table. 
 * 
 * @author yqi
 */
public abstract class Record<T extends Enum<T> & RecordMetadataInterface> implements Serializable {
		
	/**
	 * The storage metadata about the recordbuffer is final
	 */
	protected final RecordBufferMeta<T> meta; 
	
	/**
	 * The value of RecordBuffer can be changed
	 * <i> a new instance is created if its original value is NULL
	 * <i> an updating is applied if a value exists
	 */
	protected RecordBuffer buffer = null;
	
	/**
	 * Constructor when a new instance of {@link RecordBuffer} is created
	 * 
	 * @param clazz the input table class
	 */
	public Record(Class<T> clazz) {
		this.meta = RecordBufferMeta.getRecordOperator(clazz);
	}
	
	/**
	 * Constructor when a {@link RecordBuffer} instance exists (not null) 
	 * 
	 * <p>
	 * Note that the input recordbuffer instance can be updated. 
	 * </p>
	 * 
	 * @param clazz the class indicating the record metadata
	 * @param buf the non-null instance of {@link RecordBuffer}
	 */
	public Record(Class<T> clazz, RecordBuffer buf) {
		this(clazz);
		
		Preconditions.checkArgument(buf != null);
		this.buffer = buf;
	}
	
	///////////////////////////////////////////////////////////////////////////
	/// The following abstract methods must be implemented 
	///////////////////////////////////////////////////////////////////////////
	/**
	 * Update the value of the input field
	 * 
	 * @param col the field of interest
	 * @param val the new value of the concerned field
	 */
	abstract public void setValue(T col, Object val);
	abstract public void setValue(Field field, Object val);
	
	/**
	 * Get the value of the input column
	 * @param col the input field
	 * @return the value of the input column
	 */
	abstract public Object getValue(T col);
	abstract public Object getValue(Field field);
	
	
	/**
	 * Get the instance of {@link RecordBuffer} used to store all valid attributes of the record.
	 * 
	 * @return the instance of {@link RecordBuffer} used to store all valid attributes of the record.
	 */
	abstract public RecordBuffer getRecordBuffer();
	
	/**
	 * @return The number of bytes that the instance of {@link RecordBuffer} takes 
	 */
	abstract public int getNumOfBytes();
	
	/**
	 * @return An array of bytes used by the instance of {@link RecordBuffer}
	 */
	abstract public byte[] array();

	/**
	 * Get the number of elements nested in the list-type field. 
	 * 
	 * @param col the input of a list-type field.
	 * @return the number of elements nested in the list-type field.
	 */
	abstract public int getListSize(T col); 
	abstract public int getListSize(Field field); 

	///////////////////////////////////////////////////////////////////////////
	// The following getters define approaches to get the primitive when the 
	// selected column is primitive
	///////////////////////////////////////////////////////////////////////////

	abstract public boolean getBool(T col);
	abstract public byte getByte(T col);
	abstract public short getShort(T col);
	abstract public long getLong(T col);
	abstract public int getInt(T col);
	abstract public float getFloat(T col);
	abstract public double getDouble(T col);
	abstract public byte[] getBinary(T col);
	abstract public String getString(T col);
	
	abstract public boolean getBool(Field field);
	abstract public byte getByte(Field field);
	abstract public short getShort(Field field);
	abstract public long getLong(Field field);
	abstract public int getInt(Field field);
	abstract public float getFloat(Field field);
	abstract public double getDouble(Field field);
	abstract public byte[] getBinary(Field field);
	abstract public String getString(Field field);

}
