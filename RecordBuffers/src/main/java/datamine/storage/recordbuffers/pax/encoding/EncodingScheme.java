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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datamine.storage.idl.FieldValueOperatorInterface;
import datamine.storage.idl.type.FieldType;
import datamine.storage.recordbuffers.idl.value.FieldValueOperatorFactory;

/**
 * The Enum defines all encoding scheme used in the PAX format 
 * 
 * @author yqi
 */
public enum EncodingScheme {

	RunLengthCoding((byte)0, RunLengthEncoding.class),
	DictionaryCoding((byte)1, DictionaryEncoding.class),
	CommonValueCoding((byte)2, CommonValueEncoding.class),
	NoCompressionEncoding((byte)3, NoCompressionEncoding.class);

	static final Logger LOG = LoggerFactory.getLogger(EncodingScheme.class);

	private byte id;
	private Class<? extends IEncoding> encodingClass = null;

	EncodingScheme(byte id, Class<? extends IEncoding> encodingClass) {
		this.id = id;
		this.encodingClass = encodingClass;
	}

	public Class<? extends IEncoding> getEncodingClass() {
		return this.encodingClass;
	}
	
	public byte getID() {
		return this.id;
	}

	private static Map<Byte, EncodingScheme> idSchemeMap = 
			new HashMap<Byte, EncodingScheme>();

	static {
		for (EncodingScheme scheme : values()) {
			idSchemeMap.put(scheme.getID(), scheme);
		}
	}
	
	
	/**
	 * Get a list of values from the byte buffer
	 * 
	 * @param buffer the byte buffer storing the values
	 * @param offset the offset where the values start in the byte buffer
	 * @param type the type of the input field
	 * @param size the number of objects in the resulting list
	 * @return a list of values from the byte buffer in regard to the input field
	 */
	public static List<Object> getValueList(ByteBuffer buffer, int offset, FieldType type, 
			int size) {
		byte id = buffer.get(offset);
		offset++;
		EncodingScheme compressScheme = idSchemeMap.get(id);
		IEncoding encodingInst;
		try {
			encodingInst = compressScheme.encodingClass.newInstance();
			return encodingInst.getValues(buffer, offset, type, size);
		} catch (InstantiationException e) {
			LOG.error("The encoding scheme cannot be located for " + compressScheme.encodingClass, e);
		} catch (IllegalAccessException e) {
			LOG.error("The encodeing scheme cannot be accessed for " + compressScheme.encodingClass, e);
		}
		// never returning a NULL
		return new ArrayList<Object>();
	}
	

	/**
	 * It converts a list of values into an array of bytes and appends it to the given byte buffer.
	 * 
	 * @param buffer the byte buffer which the result is appended to 
	 * @param values a list of values to be converted and appended
	 * @param type the type of field involved
	 */
	public static byte[] getBytes(List<Object> values, FieldType type) {
		//1. collect the stats
		CompressStats stats = new CompressStats(values, type);

		//2. decide which scheme to use
		int minCompressDataSize = Integer.MAX_VALUE;
		IEncoding bestCompressEncoding = null;
		EncodingScheme bestCompressScheme = NoCompressionEncoding;
		for (EncodingScheme compScheme : values()) {
			try {
				IEncoding encodingInst = compScheme.encodingClass.newInstance();
				int estSize = encodingInst.getEstimatedSize(values, type, stats);
				if (estSize < minCompressDataSize) {
					estSize = minCompressDataSize;
					bestCompressEncoding = encodingInst;
					bestCompressScheme = compScheme;
				}

			} catch (InstantiationException e) {
				LOG.error("The encoding scheme cannot be located for " + compScheme.encodingClass, e);
			} catch (IllegalAccessException e) {
				LOG.error("The encodeing scheme cannot be accessed for " + compScheme.encodingClass, e);
			}
		}

		//3. write the output into the buffer using the best scheme
		byte[] content = bestCompressEncoding.getBytes(values, type, stats);
		return ByteBuffer.allocate(1 + content.length).
				put(bestCompressScheme.getID()). // id : which encoding scheme?
				put(content).					 // content : what to store?
				array();
	}


	/**
	 * Get the bytes for the input value
	 * 
	 * @param type the type of the input field
	 * @param val the value of the input field
	 * @return a byte array to contain the input value
	 */
	public static byte[] getBytes(FieldType type, Object val) {

		FieldValueOperatorInterface valueOpr = FieldValueOperatorFactory.getOperator(type);
		byte[] byteArray = val == null ? new byte[0] : valueOpr.getByteArray(val);
		if (!valueOpr.hasFixedLength()) {
			int metaSize = valueOpr.getMetadataLength();
			if (metaSize == 2) {
				if (byteArray.length > Short.MAX_VALUE) {
					LOG.error("The value is too big for a short value!");
					return new byte[]{(byte)0, (byte)0};
				} else {
					ByteBuffer buf = ByteBuffer.allocate(2+byteArray.length);
					buf.putShort((short)byteArray.length);
					buf.put(byteArray);
					return buf.array();
				}	
			} else if (metaSize == 4) {
				ByteBuffer buf = ByteBuffer.allocate(4+byteArray.length);
				buf.putInt(byteArray.length);
				buf.put(byteArray);
				return buf.array();
			}
		}
		return byteArray;
	}

	/**
	 * Read a value from a byte array
	 * 
	 * @param buffer the byte buffer containing the value
	 * @param offset the position where the value gets started
	 * @param type the type of the concerned field which the value belongs to
	 * @return a value stored in the byte buffer and the updated offset where the value ends in the byte buffer
	 */
	public static ValueAndOffset getValue(ByteBuffer buffer, int offset, FieldType type) {

		FieldValueOperatorInterface valueOpr = FieldValueOperatorFactory.getOperator(type);
		if (!valueOpr.hasFixedLength()) {
			int metaSize = valueOpr.getMetadataLength();
			int length = metaSize == 2 ? buffer.getShort(offset) : buffer.getInt(offset);
			offset += metaSize;
			return new ValueAndOffset(valueOpr.getValue(buffer, offset, length), 
					offset + length);
		} else {
			final int dummyLength = 0;
			return new ValueAndOffset(valueOpr.getValue(buffer, offset, dummyLength),
					offset + valueOpr.getNumOfBytes(null));
		}
	}

	/**
	 * It is a helper class to eliminate the duplication of reading a value from 
	 * a byte buffer.
	 * 
	 * <p>
	 * There are two members in the class:
	 * <li> value - the value of the object </li>
	 * <li> newOffset - the offset where the value ends in the byte buffer </li>
	 * </p> 
	 *  
	 * @author yqi
	 */
	static class ValueAndOffset {
		private final Object value;
		private final int newOffset;
		public ValueAndOffset(Object val, int offset) {
			this.value = val;
			this.newOffset = offset;
		}

		public Object getValue() {
			return this.value;
		}

		public int getEndOffset() {
			return newOffset;
		}
	}
}
