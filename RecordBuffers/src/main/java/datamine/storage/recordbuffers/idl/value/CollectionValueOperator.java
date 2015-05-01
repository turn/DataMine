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
package datamine.storage.recordbuffers.idl.value;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import datamine.storage.idl.FieldValueOperatorInterface;
import datamine.storage.idl.type.CollectionFieldType;
import datamine.storage.idl.type.CollectionType;
import datamine.storage.idl.type.FieldType;
import datamine.storage.idl.type.GroupFieldType;
import datamine.storage.idl.type.PrimitiveFieldType;
import datamine.storage.idl.type.PrimitiveType;

/**
 * The operation is defined for the collection type
 * 
 * @author yqi
 */
class CollectionValueOperator implements FieldValueOperatorInterface {

	private static final Logger LOG = LoggerFactory.getLogger(
			CollectionValueOperator.class);
	
	private final FieldType elementType;
	private final CollectionType collectionType;
	

	public CollectionValueOperator(CollectionFieldType type) {
		this.elementType = type.getElementType();
		this.collectionType = type.getType();
	}

	public FieldType getElementType() {
		return elementType;
	}

	public CollectionType getType() {
		return collectionType;
	}

	@Override
	public boolean isValid(Object value) {
		if (this.collectionType == CollectionType.LIST) {
			return (value instanceof List<?>);
		} 
		return false;
	}

	@Override
	public boolean hasFixedLength() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public byte[] getByteArray(Object value) {
		if (value != null) { 
			try {
				return getByteArrayForList((List<Object>) value);
			} catch (IOException e) {
				LOG.error(value + " cannot be converted into byte array");
			}
		}
		return new byte[0];
	}
		
	@Override
	public Object getValue(ByteBuffer buffer, int index, int length) {
		
		return getValueForList(buffer, index, length);
	}

	private List<Object> getValueForList(ByteBuffer buffer, int index, int length) {
	
		List<Object> ret = Lists.newArrayList();
		//1. get the size of list
		int listSize = buffer.getInt(index);
		index += 4;
		//2. start reading values
		FieldValueOperatorInterface valOpr = FieldValueOperatorFactory.getOperator(elementType);
		if (elementType instanceof PrimitiveFieldType) {
			PrimitiveType type = ((PrimitiveFieldType) elementType).getType();
			switch (type) {
			case STRING:
				for (int i = 0; i < listSize; i++) {
					short strLength = buffer.getShort(index);
					index += 2;
					if (strLength > 0) {
						ret.add(valOpr.getValue(buffer, index, strLength));
						index += strLength;	
					} else {
						ret.add(""); // TODO (Yan) default is ""?
					}
				}
				break;
			case BINARY:
				for (int i = 0; i < listSize; i++) {
					int binLength = buffer.getInt(index);
					index += 4;
					ret.add(valOpr.getValue(buffer, index, binLength));
					index += binLength;
				}
			default:
				int oLength = valOpr.getNumOfBytes(null);
				for (int i = 0; i < listSize; i++) {
					ret.add(valOpr.getValue(buffer, index, oLength));
					index += oLength;
				}
			}
		} else if (elementType instanceof GroupFieldType) {
			for (int i = 0; i < listSize; i++) {
				int rLength = buffer.getInt(index);
				index += 4;
				ret.add(valOpr.getValue(buffer, index, rLength));
				index += rLength;
			}
		}
		return ret;
	}
	
	private byte[] getByteArrayForList(List<Object> valList) throws IOException {

		if (valList == null) {
			// put the size of list (i.e., 0) in an array as the result
			return new byte[] {0, 0, 0, 0};
		}
		
		// 1. initiate the variables
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();

		// 2. fill the table
		FieldValueOperatorInterface valOpr = FieldValueOperatorFactory.getOperator(elementType);
		outStream.write(ByteBuffer.allocate(4).putInt(valList.size()).array());
		if (elementType instanceof PrimitiveFieldType) {
			PrimitiveType type = ((PrimitiveFieldType) elementType).getType();
			switch (type) {
			case STRING:
				for (Object cur : valList) {
					byte[] bytes = valOpr.getByteArray(cur);
					if (bytes.length > Short.MAX_VALUE) {
						outStream.write(new byte[]{(byte)0,(byte)0});
						LOG.error("The string is too long (>" +
								Short.MAX_VALUE + "):" + cur);
					} else {
						outStream.write(ByteBuffer.allocate(2).putShort((short) bytes.length).array());
						outStream.write(bytes);
					}
				}
				break;
			case BINARY:
				for (Object cur : valList) {
					byte[] bytes = valOpr.getByteArray(cur);
					outStream.write(ByteBuffer.allocate(4).putInt(bytes.length).array());
					outStream.write(bytes);
				}
				break;
			default:
				for (Object cur : valList) {
					outStream.write(valOpr.getByteArray(cur));
				}
			}
		} else if (elementType instanceof GroupFieldType) {
			for (Object cur : valList) {
				byte[] bytes = valOpr.getByteArray(cur);
				int len = valOpr.getNumOfBytes(cur);
				outStream.write(ByteBuffer.allocate(4).putInt(len).array());
				outStream.write(bytes, 0, len);
			}
		}
		
		return outStream.toByteArray();
	}

	@Override
	public int getNumOfBytes(Object val) {
		@SuppressWarnings("unchecked")
		List<Object> valList = (List<Object>) val;
		int length = 4; // number of 'size'
		
		if (val == null) {
			return length;
		} 
		
		// 1. get the buffer size to allocate
		int num = valList.size();
		FieldValueOperatorInterface valOpr = FieldValueOperatorFactory.getOperator(elementType);
		if (elementType instanceof PrimitiveFieldType) {
			PrimitiveType type = ((PrimitiveFieldType) elementType).getType();
			switch (type) {
			case STRING:
				for (Object cur : valList) {
					int sLen = ((String) cur).getBytes().length;
					if (sLen > Short.MAX_VALUE) {
						length += 2;
					} else {
						length += 2 + sLen;						
					}
				}
				break;
			case BINARY:
				for (Object cur : valList) {
					length += 4 + ((byte[]) cur).length;
				}
				break;
			default:
				length += num * valOpr.getNumOfBytes(null);
			}
		} else if (elementType instanceof GroupFieldType) {
			for (Object cur : valList) {
				length += 4 + valOpr.getNumOfBytes(cur);
			}
		} else if (elementType instanceof CollectionFieldType) {
			throw new IllegalArgumentException("Not support for the embedded list.");
		} else {
			throw new IllegalArgumentException("Not support for the type of " + elementType);
		}
		
		return length;
	}

	@Override
	public int getMetadataSize() {
		return 4;
	}
}
