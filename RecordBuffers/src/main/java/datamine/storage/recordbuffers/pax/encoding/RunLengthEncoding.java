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
 * It defines actions for run-length encoding approach.
 * 
 * <p>
 * For example, a list of integers, (1,2,3,3,3,3) can be stored as a byte array 
 * such that 
 * <li> '1' is stored with 1 byte for length (i.e., 1) and 4 bytes for value (i.e., 1) </li>
 * <li> '2' is stored with 1 byte for length (i.e., 2) and 4 bytes for value (i.e., 2) </li>
 * <li> '3' is stored with 1 byte for length (i.e., 4) and 4 bytes for value (i.e., 3). </li>
 * </p>
 * 
 * <p>
 * Note that to use the space wisely, the byte to record the running length is 
 * treated unsigned (0 ~ 255). 
 * </p>
 * 
 * @author yqi
 */
class RunLengthEncoding implements IEncoding {

	static final Logger LOG = LoggerFactory.getLogger(RunLengthEncoding.class);

	static final int MAX_LENGTH = 2 * MAX_BYTE;

	@Override
	public int getEstimatedSize(List<Object> values, FieldType type, CompressStats stats) {
		return stats.getRunLengthDataSize();
	}


	@Override
	public byte[] getBytes(List<Object> values, FieldType type, CompressStats stats) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		Object currentValue = null;
		int runLength = -1; // length [1, 256] store as [Byte.MIN_VALUE, Byte.MAX_VALUE]=[-128, 127]: (length - byteOffset - 1)
		try {
			for (Object val : values) {
				if (currentValue == null) {
					currentValue = val;
					runLength = 1;
				} else {
					if ((currentValue == null && val == null) || currentValue.equals(val)) {
						runLength++;
						// overflow
						if (runLength > MAX_LENGTH) {
							outStream.write((byte) (MAX_BYTE - 1));
							outStream.write(EncodingScheme.getBytes(type, currentValue));
							runLength = 1;
						}
					} else {
						outStream.write((byte) (runLength - MAX_BYTE - 1));
						outStream.write(EncodingScheme.getBytes(type, currentValue));
						runLength = 1;
						currentValue = val;
					}
				}
			}
			outStream.write((byte) (runLength - MAX_BYTE - 1));
			outStream.write(EncodingScheme.getBytes(type, currentValue));
			byte[] result = outStream.toByteArray();
			outStream.close();
			return result;
		} catch (IOException e) {
			LOG.error("An error occurs when generating the byte array for output", e);
			return new byte[0];
		}
	}

	@Override
	public List<Object> getValues(ByteBuffer buffer, int offset, FieldType type, int size) {

		//2. get the values
		List<Object> values = Lists.newArrayList();

		Object currentValue = null;
		while (size > 0) {
			//2.1 read the LENGTH
			int repNum = buffer.get(offset) + MAX_BYTE + 1;
			offset++;

			//2.2 get the value
			ValueAndOffset vao = EncodingScheme.getValue(buffer, offset, type);
			currentValue = vao.getValue();
			offset = vao.getEndOffset();
			
			//2.3 add the value in the result list
			for (int i=0; i<repNum; ++i) {
				values.add(currentValue);
				size--;
			}
		}

		return values;
	}

	@Override
	public int[] getIntValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		int[] values = new int[size];
		for (int i=0; i<size;) {
			int repNum = buffer.get() + MAX_BYTE + 1;
			int value = buffer.getInt();
			for (int j=0; j<repNum; ++j) {
				values[i++] = value;
			}
		}
		return values;
	}

	@Override
	public short[] getShortValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		short[] values = new short[size];
		for (int i=0; i<size;) {
			int repNum = buffer.get() + MAX_BYTE + 1;
			short value = buffer.getShort();
			for (int j=0; j<repNum; ++j) {
				values[i++] = value;
			}
		}
		return values;
	}

	@Override
	public long[] getLongValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		long[] values = new long[size];
		for (int i=0; i<size;) {
			int repNum = buffer.get() + MAX_BYTE + 1;
			long value = buffer.getLong();
			for (int j=0; j<repNum; ++j) {
				values[i++] = value;
			}
		}
		return values;
	}

	@Override
	public byte[] getByteValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		byte[] values = new byte[size];
		for (int i=0; i<size;) {
			int repNum = buffer.get() + MAX_BYTE + 1;
			byte value = buffer.get();
			for (int j=0; j<repNum; ++j) {
				values[i++] = value;
			}
		}
		return values;
	}

	@Override
	public boolean[] getBoolValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		boolean[] values = new boolean[size];
		for (int i=0; i<size;) {
			int repNum = buffer.get() + MAX_BYTE + 1;
			boolean value = buffer.get() == 1 ? true : false;
			for (int j=0; j<repNum; ++j) {
				values[i++] = value;
			}
		}
		return values;
	}

	@Override
	public float[] getFloatValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		float[] values = new float[size];
		for (int i=0; i<size;) {
			int repNum = buffer.get() + MAX_BYTE + 1;
			float value = buffer.getFloat();
			for (int j=0; j<repNum; ++j) {
				values[i++] = value;
			}
		}
		return values;
	}

	@Override
	public double[] getDoubleValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		double[] values = new double[size];
		for (int i=0; i<size;) {
			int repNum = buffer.get() + MAX_BYTE + 1;
			double value = buffer.getDouble();
			for (int j=0; j<repNum; ++j) {
				values[i++] = value;
			}
		}
		return values;
	}

	@Override
	public String[] getStringValues(ByteBuffer buffer, int offset, int size) {
		String[] values = new String[size];
		for (int i=0; i<size;) {
			int repNum = buffer.get(offset) + MAX_BYTE + 1;
			offset++;
			int length = buffer.getShort(offset);
			offset += 2;
			String value = new String(buffer.array(), offset, length);
			offset += length;
			for (int j=0; j<repNum; ++j, ++i) {
				values[i] = value;
			}
		}
		return values;
	}

	@Override
	public byte[][] getBinaryValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		byte[][] values = new byte[size][];
		for (int i=0; i<size;) {
			int repNum = buffer.get() + MAX_BYTE + 1;
			int length = buffer.getInt();
			byte[] value = new byte[length];
			buffer.get(value);
			for (int j=0; j<repNum; ++j, ++i) {
				values[i] = new byte[length];
				System.arraycopy(value, 0, values[i], 0, length);
			}
		}
		return values;
	}
}
