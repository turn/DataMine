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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import datamine.storage.idl.FieldValueOperatorInterface;
import datamine.storage.idl.type.FieldType;
import datamine.storage.recordbuffers.idl.value.FieldValueOperatorFactory;

/**
 * The class collects the statistics of values through which the right encoding 
 * scheme can be determined. 
 * 
 * <p>
 * Particularly it aims to iterate a list of input objects to get the following 
 * information: 
 * 
 * <li>The number of bytes to store all objects in sequence without any compression</li>
 * <li>The frequencies of input objects</li>
 * <li>The most commonly used object and its frequency</li>
 * <li>The number of bytes to store all objects using the run-length encoding scheme</li>
 * <li></li>
 * 
 * </p>
 *  
 * @author yqi
 */
class CompressStats {
	
	public static final Logger LOG = LoggerFactory.getLogger(CompressStats.class);
		
	private int totalDataSize = 0;
	private Map<Object, Integer> freqMap = new HashMap<Object, Integer>();
	private int commonestFreq = 0;
	private Object commonestValue = null;
	private int runLengthSize = 0;

	public CompressStats(List<Object> values, FieldType type) {
		
		FieldValueOperatorInterface valueOpr = FieldValueOperatorFactory.getOperator(type);

		// Go over the value list to get frequency and size
		Object curValue = null;
		boolean isFirst = true;
		int runLength = 0;
		for(Object obj : values) {
			// collect information about frequency
			Integer freq = freqMap.get(obj);
			if (freq == null) {
				freqMap.put(obj, 1);
			} else {
				freqMap.put(obj, freq+1);
			}
			
			// collect the size information for the runlength encoding 
			int valSize = getIncrSize(valueOpr, obj);
			if (isFirst) {
				isFirst = false;
				curValue = obj;
				runLength = 1;
				runLengthSize += 1 + valSize;
			} else {
				if ((curValue == null && obj == null) || 
					(curValue != null && curValue.equals(obj))) {
					runLength++;
					if (runLength > RunLengthEncoding.MAX_LENGTH) {
						runLengthSize += 1 + valSize;
						runLength = 1;
					}
				} else {
					runLengthSize += 1 + valSize;
					runLength = 1;
					curValue = obj;
				}
			}
			
			// find out the size of all data 
			totalDataSize += valSize;
		}	
		
		// find out the value occurring the most
		for (Object hash : freqMap.keySet()) {
			if (freqMap.get(hash) > commonestFreq) {
				commonestFreq = freqMap.get(hash);
				commonestValue = hash;
			}
		}
	}
	
	private int getIncrSize(FieldValueOperatorInterface opr, Object value) {
		if (opr.hasFixedLength()) {
			return opr.getNumOfBytes(null);
		} else {
			return opr.getMetadataLength() + opr.getByteArray(value).length; 
		}
	}
	
	/**
	 * Get the number of bytes used to store a list of objects
	 * @return the number of bytes used to store a list of objects
	 */
	public int getRunLengthDataSize() {
		return runLengthSize;
	}
	
	/**
	 * @return the totalDataSize
	 */
	public int getTotalDataSize() {
		return totalDataSize;
	}

	/**
	 * @return the freqMap
	 */
	public Map<Object, Integer> getFreqMap() {
		return freqMap;
	}

	/**
	 * @return the commonestFreq
	 */
	public int getCommonestFreq() {
		return commonestFreq;
	}

	/**
	 * @return the commonestValue
	 */
	public Object getCommonestValue() {
		return commonestValue;
	}

}