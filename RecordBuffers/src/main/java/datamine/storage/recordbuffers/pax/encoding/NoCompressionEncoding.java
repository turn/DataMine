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
 * It doesn't apply any compression approach
 * 
 * @author yqi
 */
class NoCompressionEncoding implements IEncoding {
	
	static final Logger LOG = LoggerFactory.getLogger(NoCompressionEncoding.class);
			
	@Override
	public int getEstimatedSize(List<Object> values, FieldType type, CompressStats stats) {
		return stats.getTotalDataSize();
	}
	
	@Override
	public byte[] getBytes(List<Object> values, FieldType type,
			CompressStats stats) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			for (Object cur : values) {
				outStream.write(EncodingScheme.getBytes(type, cur));
			}
			byte[] out = outStream.toByteArray();
			outStream.close();
			return out;
		} catch (IOException e) {
			LOG.error("An error occurs when generating the byte array for output", e);
			return new byte[0];
		}
	}

	@Override
	public List<Object> getValues(ByteBuffer buffer, int offset, FieldType type, int size) {
		List<Object> values = Lists.newArrayList();
		for (int index = 0; index < size; ++index) {
			ValueAndOffset vao = EncodingScheme.getValue(buffer, offset, type);
			values.add(vao.getValue());
			offset = vao.getEndOffset();
		}
		return values;
	}

	@Override
	public int[] getIntValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		int[] values = new int[size];
		for (int index = 0; index < size; ++index) {
			values[index] = buffer.getInt();
		}
		return values;
	}

	@Override
	public short[] getShortValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		short[] values = new short[size];
		for (int index = 0; index < size; ++index) {
			values[index] = buffer.getShort();
		}
		return values;
	}

	@Override
	public long[] getLongValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		long[] values = new long[size];
		for (int index = 0; index < size; ++index) {
			values[index] = buffer.getLong();
		}
		return values;
	}

	@Override
	public byte[] getByteValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		byte[] values = new byte[size];
		for (int index = 0; index < size; ++index) {
			values[index] = buffer.get();
		}
		return values;
	}

	@Override
	public boolean[] getBoolValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		boolean[] values = new boolean[size];
		for (int index = 0; index < size; ++index) {
			values[index] = buffer.get() == 1 ? true : false;
		}
		return values;
	}

	@Override
	public float[] getFloatValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		float[] values = new float[size];
		for (int index = 0; index < size; ++index) {
			values[index] = buffer.getFloat();
		}
		return values;
	}

	@Override
	public double[] getDoubleValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		double[] values = new double[size];
		for (int index = 0; index < size; ++index) {
			values[index] = buffer.getDouble();
		}
		return values;
	}

	@Override
	public String[] getStringValues(ByteBuffer buffer, int offset, int size) {
		String[] values = new String[size];
		for (int index = 0; index < size; ++index) {
			int length = buffer.getShort(offset);
			values[index] = new String(buffer.array(), offset + 2, length);
			offset += 2 + length;
		}
		return values;
	}

	@Override
	public byte[][] getBinaryValues(ByteBuffer buffer, int offset, int size) {
		buffer.position(offset);
		byte[][] values = new byte[size][];
		for (int index = 0; index < size; ++index) {
			int length = buffer.getInt();
			values[index] = new byte[length];
			buffer.get(values[index]);
		}
		return values;
	}
	
}
