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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import datamine.storage.idl.FieldValueOperatorInterface;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.idl.type.PrimitiveType;
import datamine.storage.recordbuffers.idl.value.FieldValueOperatorFactory;
import datamine.storage.recordbuffers.pax.encoding.EncodingScheme.ValueAndOffset;

/**
 * The class defines the dictionary encoding scheme. 
 * 
 * <p>
 * Note that it only supports the following types:
 * <li>STRING</li>
 * <li>LONG</li>
 * <li>INTEGER</li>
 * <li>SHORT</li> 
 * </p>
 * 
 * @author yqi
 */
class DictionaryEncoding implements IEncoding {

	static final Logger LOG = LoggerFactory.getLogger(DictionaryEncoding.class);

	private static final int MAX_LENGTH = 2 * MAX_SHORT;
	private static final int BYTE_NUM_FOR_VALUE_LIST = 2; //the number of entries (i.e., 2 bytes) in the dictionary [0, MAX_LENGTH]

	private static final EnumSet<PrimitiveType> validSet = EnumSet.of( 
			PrimitiveType.BOOL, PrimitiveType.FLOAT, PrimitiveType.DOUBLE);
	
	@Override
	public int getEstimatedSize(List<Object> values, FieldType type, CompressStats stats) {
		
		// 1. ensure the input field type is supported
		if (type instanceof PrimitiveFieldType) {
			if (validSet.contains(((PrimitiveFieldType) type).getPrimitiveType())) {
				return Integer.MAX_VALUE; // not a supported type 
			}
		}
		
		// 2. estimate the reasonable size
		int dictionarySize = stats.getFreqMap().size();
		int numOfByte4value = dictionarySize <= MAX_BYTE * 2 ? 1 : 2;
		if (dictionarySize > MAX_LENGTH) {
			return Integer.MAX_VALUE; // too big to use!
		} else {
			FieldValueOperatorInterface valueOpr = FieldValueOperatorFactory.getOperator(type);
			if (!valueOpr.hasFixedLength()) {
				int metaHeaderSize = valueOpr.getMetadataLength();
				int size = 0;
				for (Object key : stats.getFreqMap().keySet()) {
					size += (metaHeaderSize + valueOpr.getByteArray(key).length);
				}
				return BYTE_NUM_FOR_VALUE_LIST + size + 
						values.size() * numOfByte4value;
			} else {
				return BYTE_NUM_FOR_VALUE_LIST + dictionarySize * valueOpr.getNumOfBytes(null) + 
						values.size() * numOfByte4value;
			}
		}
	}

	@Override
	public List<Object> getValues(ByteBuffer buffer, int offset,
			FieldType type, int size) {
		List<Object> values = Lists.newArrayList();
		//1. Get dictionary
		int dicSize = buffer.getShort(offset) + MAX_SHORT + 1;
		offset += 2;
		Object[] dictionary = new Object[dicSize];
		for (int i = 0; i < dicSize; i++) {
			ValueAndOffset vao = EncodingScheme.getValue(buffer, offset, type);
			dictionary[i] = vao.getValue();
			offset = vao.getEndOffset();
		}

		//2. Read all values into the list
		for (int index = 0; index < size; ++index) {
			if (dictionary.length > MAX_BYTE * 2) {
				values.add(dictionary[buffer.getShort(offset) + MAX_SHORT]);
				offset += 2;
			} else {
				values.add(dictionary[buffer.get(offset) + MAX_BYTE]);
				offset++;
			}
		}
		return values;
	}

	@Override
	public byte[] getBytes(List<Object> values, FieldType type,
			CompressStats stats) {

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			//1. store the dictionary size (no larger than the max short value)
			int size = stats.getFreqMap().size(); // [1, 65536];
			outStream.write(ByteBuffer.allocate(2).putShort(
					(short)(size - MAX_SHORT - 1)).array());// [-32768, 32767]

			//2. write the dictionary first
			int id = 0;
			Map<Object, Integer> valueIndex = Maps.newHashMap();
			for (Object key : stats.getFreqMap().keySet()) {
				outStream.write(EncodingScheme.getBytes(type, key));
				valueIndex.put(key, id++);
			}

			//3. write the value list next
			if (size <= MAX_BYTE * 2) {
				// index: [0, 255] stored as [-128, 127]: index - byteOffset;
				for (Object val : values) {
					outStream.write((byte) (valueIndex.get(val) - MAX_BYTE));
				}
			} else {
				// index: [0, 65535] stored as [-32768, 32767]: index - shortOffset;
				for (Object val : values) {
					outStream.write(ByteBuffer.allocate(2).putShort(
							(short) (valueIndex.get(val) - MAX_SHORT)).array());
				}
			}

			//4. result
			byte[] result = outStream.toByteArray();
			outStream.close();

			return result;

		} catch (IOException e) {
			LOG.error("An error occurs when generating the byte array for output", e);
			return new byte[0];
		}
	}

	@Override
	public int[] getIntValues(ByteBuffer buffer, int offset, int size) {

		//1. Get dictionary
		buffer.position(offset);
		int dicSize = buffer.getShort() + MAX_SHORT + 1;
		int[] dictionary = new int[dicSize];
		for (int i = 0; i < dicSize; i++) {
			dictionary[i] = buffer.getInt();
		}

		//2. Read all values into the list
		int[] values = new int[size];
		for (int index = 0; index < size; ++index) {
			if (dictionary.length > MAX_BYTE * 2) {
				values[index] = dictionary[buffer.getShort() + MAX_SHORT];
			} else {
				values[index] = dictionary[buffer.get() + MAX_BYTE];
			}
		}
		return values;
	}

	@Override
	public short[] getShortValues(ByteBuffer buffer, int offset, int size) {
		//1. Get dictionary
		buffer.position(offset);
		int dicSize = buffer.getShort() + MAX_SHORT + 1;
		short[] dictionary = new short[dicSize];
		for (int i = 0; i < dicSize; i++) {
			dictionary[i] = buffer.getShort();
		}

		//2. Read all values into the list
		short[] values = new short[size];
		for (int index = 0; index < size; ++index) {
			if (dictionary.length > MAX_BYTE * 2) {
				values[index] = dictionary[buffer.getShort() + MAX_SHORT];
			} else {
				values[index] = dictionary[buffer.get() + MAX_BYTE];
			}
		}
		return values;
	}

	@Override
	public long[] getLongValues(ByteBuffer buffer, int offset, int size) {
		//1. Get dictionary
		buffer.position(offset);
		int dicSize = buffer.getShort() + MAX_SHORT + 1;
		long[] dictionary = new long[dicSize];
		for (int i = 0; i < dicSize; i++) {
			dictionary[i] = buffer.getLong();
		}

		//2. Read all values into the list
		long[] values = new long[size];
		for (int index = 0; index < size; ++index) {
			if (dictionary.length > MAX_BYTE * 2) {
				values[index] = dictionary[buffer.getShort() + MAX_SHORT];
			} else {
				values[index] = dictionary[buffer.get() + MAX_BYTE];
			}
		}
		return values;
	}

	@Override
	public byte[] getByteValues(ByteBuffer buffer, int offset, int size) {
		throw new IllegalAccessError("No support for the dictionary encoding on byte");
	}

	@Override
	public boolean[] getBoolValues(ByteBuffer buffer, int offset, int size) {
		throw new IllegalAccessError("No support for the dictionary encoding on bool");
	}

	@Override
	public float[] getFloatValues(ByteBuffer buffer, int offset, int size) {
		throw new IllegalAccessError("No support for the dictionary encoding on float");
	}

	@Override
	public double[] getDoubleValues(ByteBuffer buffer, int offset, int size) {
		throw new IllegalAccessError("No support for the dictionary encoding on double");
	}

	@Override
	public String[] getStringValues(ByteBuffer buffer, int offset, int size) {
		//1. Get dictionary
		buffer.position(offset);
		int dicSize = buffer.getShort() + MAX_SHORT + 1;
		String[] dictionary = new String[dicSize];
		for (int i = 0; i < dicSize; i++) {
			int length = buffer.getShort();
			byte[] str = new byte[length];
			buffer.get(str);
			dictionary[i] = new String(str, 0, str.length);
		}

		//2. Read all values into the list
		String[] values = new String[size];
		for (int index = 0; index < size; ++index) {
			if (dictionary.length > MAX_BYTE * 2) {
				values[index] = dictionary[buffer.getShort() + MAX_SHORT];
			} else {
				values[index] = dictionary[buffer.get() + MAX_BYTE];
			}
		}
		return values;
	}

	@Override
	public byte[][] getBinaryValues(ByteBuffer buffer, int offset, int size) {
		throw new IllegalAccessError("No support for the dictionary encoding on binary");
	}
}
