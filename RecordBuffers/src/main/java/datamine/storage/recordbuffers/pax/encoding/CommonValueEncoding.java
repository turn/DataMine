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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import datamine.storage.idl.type.FieldType;
import datamine.storage.recordbuffers.pax.encoding.EncodingScheme.ValueAndOffset;

/**
 * It defines the common value encoding approach.
 * 
 * <p>
 * To store a list of values with the common value encoding, it stores
 * <li> a bitmask to indicate the positions where the common value occurs </li>
 * <li> the common value itself, and </li>
 * <li> the rest of values in the list</li>
 * </p>
 *
 * 
 * @author yqi
 */
class CommonValueEncoding implements IEncoding {

	static final Logger LOG = LoggerFactory.getLogger(CommonValueEncoding.class);

	@Override
	public int getEstimatedSize(List<Object> values, FieldType type, CompressStats stats) {
		int size = values.size();
		int length = EncodingScheme.getBytes(type, stats.getCommonestValue()).length;
		return stats.getTotalDataSize() - length * (stats.getCommonestFreq() - 1) + (size + 7) / 8;
	}


	@Override
	public List<Object> getValues(ByteBuffer buffer, int offset,
			FieldType type, int size) {
		List<Object> values = Lists.newArrayList();

		//1. Collect 'flag' for the most commonest value 
		byte[] flags = new byte[(size + 7) / 8];
		buffer.position(offset);
		buffer.get(flags);
		offset += flags.length;

		//2. Fetch the commonest value;
		ValueAndOffset vao = EncodingScheme.getValue(buffer, offset, type);
		Object commonest = vao.getValue();
		offset = vao.getEndOffset();

		//3. read the rest
		for (int index = 0; index < size; ++index) {

			byte flag = flags[index / 8];
			byte bit = (byte) (1 << ((7 - (index % 8))));
			if ((flag & bit) != 0) {
				values.add(commonest);
			} else {
				vao = EncodingScheme.getValue(buffer, offset, type);
				values.add(vao.getValue());
				offset = vao.getEndOffset();
			}
		}
		return values;
	}

	@Override
	public byte[] getBytes(List<Object> values, FieldType type,
			CompressStats stats) {

		//1. initiate variables
		int size = values.size();
		Object commonestValue = stats.getCommonestValue();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		try {
			//2. prepare the flag where the commonest value occurs
			byte[] flag = new byte[(size + 7) / 8];
			outStream.write(flag);

			//3. put the commonest value now
			byte[] commonestValueBytes = EncodingScheme.getBytes(type, commonestValue);
			outStream.write(commonestValueBytes);

			//4. go through the input list to write values
			for (int j = 0; j < size; j++) {
				if ((commonestValue == null && values.get(j) == null) || commonestValue.equals(values.get(j))) {
					flag[j / 8] |= (byte) (1 << (7 - (j % 8)));
				} else {
					outStream.write(EncodingScheme.getBytes(type, values.get(j)));
				}
			}

			//5. get the output 
			byte[] result = outStream.toByteArray();
			outStream.close();

			//6. update the 'flag' at the beginning
			System.arraycopy(flag, 0, result, 0, flag.length);

			return result;

		} catch (IOException e) {
			LOG.error("An error occurs when generating the byte array for output", e);
			return new byte[0];
		}

	}

	@Override
	public int[] getIntValues(ByteBuffer buffer, int offset, int size) {
		//1. Collect 'flag' for the most commonest value 
		byte[] flags = new byte[(size + 7) / 8];
		buffer.position(offset);
		buffer.get(flags, 0, flags.length);

		//2. Fetch the commonest value;
		int commonest = buffer.getInt();

		//3. read the rest
		int[] values = new int[size];
		for (int index = 0; index < size; ++index) {
			byte flag = flags[index / 8];
			byte bit = (byte) (1 << ((7 - (index % 8))));
			if ((flag & bit) != 0) {
				values[index] = commonest;
			} else {
				values[index] = buffer.getInt();
			}
		}
		return values;
	}

	@Override
	public short[] getShortValues(ByteBuffer buffer, int offset, int size) {
		//1. Collect 'flag' for the most commonest value 
		byte[] flags = new byte[(size + 7) / 8];
		buffer.position(offset);
		buffer.get(flags, 0, flags.length);

		//2. Fetch the commonest value;
		short commonest = buffer.getShort();

		//3. read the rest
		short[] values = new short[size];
		for (int index = 0; index < size; ++index) {
			byte flag = flags[index / 8];
			byte bit = (byte) (1 << ((7 - (index % 8))));
			if ((flag & bit) != 0) {
				values[index] = commonest;
			} else {
				values[index] = buffer.getShort();
			}
		}
		return values;
	}

	@Override
	public long[] getLongValues(ByteBuffer buffer, int offset, int size) {
		//1. Collect 'flag' for the most commonest value 
		byte[] flags = new byte[(size + 7) / 8];
		buffer.position(offset);
		buffer.get(flags, 0, flags.length);

		//2. Fetch the commonest value;
		long commonest = buffer.getLong();

		//3. read the rest
		long[] values = new long[size];
		for (int index = 0; index < size; ++index) {
			byte flag = flags[index / 8];
			byte bit = (byte) (1 << ((7 - (index % 8))));
			if ((flag & bit) != 0) {
				values[index] = commonest;
			} else {
				values[index] = buffer.getLong();
			}
		}
		return values;	}

	@Override
	public byte[] getByteValues(ByteBuffer buffer, int offset, int size) {
		//1. Collect 'flag' for the most commonest value 
		byte[] flags = new byte[(size + 7) / 8];
		buffer.position(offset);
		buffer.get(flags, 0, flags.length);

		//2. Fetch the commonest value;
		byte commonest = buffer.get();

		//3. read the rest
		byte[] values = new byte[size];
		for (int index = 0; index < size; ++index) {
			byte flag = flags[index / 8];
			byte bit = (byte) (1 << ((7 - (index % 8))));
			if ((flag & bit) != 0) {
				values[index] = commonest;
			} else {
				values[index] = buffer.get();
			}
		}
		return values;
	}

	@Override
	public boolean[] getBoolValues(ByteBuffer buffer, int offset, int size) {
		//1. Collect 'flag' for the most commonest value 
		byte[] flags = new byte[(size + 7) / 8];
		buffer.position(offset);
		buffer.get(flags, 0, flags.length);

		//2. Fetch the commonest value;
		boolean commonest = buffer.get() == 1 ? true : false;

		//3. read the rest
		boolean[] values = new boolean[size];
		for (int index = 0; index < size; ++index) {
			byte flag = flags[index / 8];
			byte bit = (byte) (1 << ((7 - (index % 8))));
			if ((flag & bit) != 0) {
				values[index] = commonest;
			} else {
				values[index] = buffer.get() == 1 ? true : false;
			}
		}
		return values;
	}

	@Override
	public float[] getFloatValues(ByteBuffer buffer, int offset, int size) {
		//1. Collect 'flag' for the most commonest value 
		byte[] flags = new byte[(size + 7) / 8];
		buffer.position(offset);
		buffer.get(flags, 0, flags.length);

		//2. Fetch the commonest value;
		float commonest = buffer.getFloat();

		//3. read the rest
		float[] values = new float[size];
		for (int index = 0; index < size; ++index) {
			byte flag = flags[index / 8];
			byte bit = (byte) (1 << ((7 - (index % 8))));
			if ((flag & bit) != 0) {
				values[index] = commonest;
			} else {
				values[index] = buffer.getFloat();
			}
		}
		return values;
	}

	@Override
	public double[] getDoubleValues(ByteBuffer buffer, int offset, int size) {
		//1. Collect 'flag' for the most commonest value 
		byte[] flags = new byte[(size + 7) / 8];
		buffer.position(offset);
		buffer.get(flags, 0, flags.length);

		//2. Fetch the commonest value;
		double commonest = buffer.getDouble();

		//3. read the rest
		double[] values = new double[size];
		for (int index = 0; index < size; ++index) {
			byte flag = flags[index / 8];
			byte bit = (byte) (1 << ((7 - (index % 8))));
			if ((flag & bit) != 0) {
				values[index] = commonest;
			} else {
				values[index] = buffer.getDouble();
			}
		}
		return values;
	}

	@Override
	public String[] getStringValues(ByteBuffer buffer, int offset, int size) {

		//1. Collect 'flag' for the most commonest value 
		byte[] flags = new byte[(size + 7) / 8];
		buffer.position(offset);
		buffer.get(flags);
		offset += flags.length;

		//2. Fetch the commonest value;
		int strLength = buffer.getShort(offset);
		offset += 2;
		String commonest = new String(buffer.array(), offset, strLength);
		offset += strLength;

		//3. read the rest
		String[] values = new String[size];
		for (int index = 0; index < size; ++index) {
			byte flag = flags[index / 8];
			byte bit = (byte) (1 << ((7 - (index % 8))));
			if ((flag & bit) != 0) {
				values[index] = commonest;
			} else {
				strLength = buffer.getShort(offset);
				offset += 2;
				values[index] = new String(buffer.array(), offset, strLength);
				offset += strLength;
			}
		}
		return values;
	}

	@Override
	public byte[][] getBinaryValues(ByteBuffer buffer, int offset, int size) {
		//1. Collect 'flag' for the most commonest value 
		byte[] flags = new byte[(size + 7) / 8];
		buffer.position(offset);
		buffer.get(flags);
		offset += flags.length;

		//2. Fetch the commonest value;
		int binaryLength = buffer.getInt(offset);
		offset += 4;
		byte[] commonest = new byte[binaryLength];
		buffer.position(offset);
		buffer.get(commonest);
		offset += binaryLength;

		//3. read the rest
		byte[][] values = new byte[size][];
		for (int index = 0; index < size; ++index) {
			byte flag = flags[index / 8];
			byte bit = (byte) (1 << ((7 - (index % 8))));
			if ((flag & bit) != 0) {
				byte[] value = new byte[commonest.length];
				System.arraycopy(commonest, 0, value, 0, value.length);
				values[index] = value;
			} else {
				binaryLength = buffer.getInt(offset);
				offset += 4;
				values[index] = new byte[binaryLength];
				buffer.position(offset);
				buffer.get(values[index]);
				offset += binaryLength;
			}
		}
		return values;
	}
}
