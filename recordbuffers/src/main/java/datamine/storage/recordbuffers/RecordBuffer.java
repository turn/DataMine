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

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * The implementation of storage for a record. 
 * 
 * @author yqi
 * @date Jul 31, 2014
 */
public class RecordBuffer implements Writable {
	
	/**
	 * Size of buffer	
	 */
	private int recordByteBufferSize = 0;
		
	/**
	 * The buffer holding all records
	 */
	private ByteBuffer recordByteBuffer = null;
	
	/**
	 * This is a dummy constructor for Hadoop MR job
	 */
	public RecordBuffer() {
		
	} 
	
	public RecordBuffer(RecordBuffer record) {
		this(record.getByteBuffer().array(), 0, record.getRecordBufferSize());
	}
	
	public RecordBuffer(byte[] recordBytes, int offset, int length) {
		Preconditions.checkArgument(length > 0);
		this.recordByteBufferSize = length;
		this.recordByteBuffer = ByteBuffer.allocate(length);
		this.recordByteBuffer.put(recordBytes, offset, length);
	}	

	public void wrap(byte[] recordBytes, int offset, int length) {
		Preconditions.checkArgument(length > 0);
		this.recordByteBufferSize = length;
		this.recordByteBuffer = ByteBuffer.wrap(recordBytes, offset, length);
	}

	/**
	 * FIXME is it right, always?
	 * @return the recordBufferSize
	 */
	public int getRecordBufferSize() {
		return this.recordByteBufferSize;
	}

	/**
	 * @return the recordBuffer
	 */
	public ByteBuffer getByteBuffer() {
		return recordByteBuffer;
	}

	public void clear() {
		this.recordByteBuffer = null;
		this.recordByteBufferSize = 0;
	}
	
	@Override
	public void readFields(DataInput in) throws IOException {
		this.recordByteBufferSize = in.readInt();
		if (this.recordByteBuffer == null || this.recordByteBufferSize > this.recordByteBuffer.capacity()) {
			this.recordByteBuffer = ByteBuffer.allocate(this.recordByteBufferSize);
		} else {
			this.recordByteBuffer.clear();
		}
		
		in.readFully(this.recordByteBuffer.array(), 0, this.recordByteBufferSize);
	}

	@Override
	public void write(DataOutput out) throws IOException {
		out.writeInt(this.recordByteBufferSize);
		out.write(this.recordByteBuffer.array(), 0, this.recordByteBufferSize);
	}

}
