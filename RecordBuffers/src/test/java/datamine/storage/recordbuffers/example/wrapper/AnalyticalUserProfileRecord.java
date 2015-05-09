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
package datamine.storage.recordbuffers.example.wrapper;

import datamine.storage.recordbuffers.example.model.*;
import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.storage.api.BaseInterface;
import datamine.storage.recordbuffers.*;

import java.nio.ByteBuffer;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import com.google.common.base.Strings;



/**
 * DO NOT CHANGE! Auto-generated code
 */
public class AnalyticalUserProfileRecord implements AnalyticalUserProfileInterface {
    static final Logger LOG = LoggerFactory.getLogger(AnalyticalUserProfileRecord.class);

    Record<AnalyticalUserProfileMetadata> value = null;

    public AnalyticalUserProfileRecord() {
        value = new Record<AnalyticalUserProfileMetadata>(AnalyticalUserProfileMetadata.class);
    }

    public AnalyticalUserProfileRecord(Record<AnalyticalUserProfileMetadata> value) {
        this.value = value;
    }

    @Override
    public Object getBaseObject() {
        return value;
    }

    @Override
    public Class getBaseClass() {
        return Record.class;
    }

    @Override
    public void setBaseObject(Object obj) {
        if (obj instanceof Record){
            this.value = (Record<AnalyticalUserProfileMetadata>) obj;
        }else{
            throw new IllegalArgumentException("Not Support type of "+obj.getClass());
        }
    }

    @Override
    public void referTo(BaseInterface right) {
        this.value = (Record<AnalyticalUserProfileMetadata>) right.getBaseObject();
    }

    @Override
    public void copyFrom(BaseInterface right) {
		// note that it may not be deep copy!!
		this.value = new Record<AnalyticalUserProfileMetadata>(AnalyticalUserProfileMetadata.class, 
			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));
    }

    @Override
    public boolean equals(Object that) {
        if (that == null){
            return false;
        }
        if (that instanceof AnalyticalUserProfileRecord){
            return this.getBaseObject().equals(((AnalyticalUserProfileRecord) that).getBaseObject());
        }
        return false;
    }

    @Override
    public long getUserId() {
        
        return (Long) this.value.getValue(AnalyticalUserProfileMetadata.USER_ID);
    }

    @Override
    public byte getVersion() {
        
        return (Byte) this.value.getValue(AnalyticalUserProfileMetadata.VERSION);
    }

    @Override
    public short getResolution() {
        
        return (Short) this.value.getValue(AnalyticalUserProfileMetadata.RESOLUTION);
    }

    @Override
    public String getOsVersion() {
        
        return (String) this.value.getValue(AnalyticalUserProfileMetadata.OS_VERSION);
    }

    @Override
    public List<ImpressionInterface> getImpressions() {
            	List<ImpressionInterface> dList = Lists.newArrayList();
		List<Object> sList = (List<Object>) this.value.getValue(AnalyticalUserProfileMetadata.IMPRESSIONS);
		if(sList != null) {
		   	for (Object cur : sList) {
    			dList.add(new ImpressionRecord((Record)cur));
	    	}
		}
       return dList;

        
    }

    @Override
    public IdMapInterface getIdMaps() {
        
        
		Record record = (Record) this.value.getValue(AnalyticalUserProfileMetadata.ID_MAPS);
		if (record == null) {
			return null;
		} else {
			return new IdMapRecord(record);
		}

    }

    @Override
    public List<Integer> getTimeList() {
            	List<Integer> dList = Lists.newArrayList();
		List<Object> sList = (List<Object>) this.value.getValue(AnalyticalUserProfileMetadata.TIME_LIST);
		if(sList != null) {
		   	for (Object cur : sList) {
    			dList.add((Integer) cur);
	    	}
		}
       return dList;

        
    }

    @Override
    public String getDay() {
        
        return (String) this.value.getValue(AnalyticalUserProfileMetadata.DAY);
    }



	@Override
	public void setUserId(long input) {
		if (1 == 1) {
			
			this.value.setValue(AnalyticalUserProfileMetadata.USER_ID, input);
		}
	}


	@Override
	public void setVersion(byte input) {
		if (1 == 1) {
			
			this.value.setValue(AnalyticalUserProfileMetadata.VERSION, input);
		}
	}


	@Override
	public void setResolution(short input) {
		if (1 == 1) {
			
			this.value.setValue(AnalyticalUserProfileMetadata.RESOLUTION, input);
		}
	}


	@Override
	public void setOsVersion(String input) {
		if (1 == 1) {
			
			this.value.setValue(AnalyticalUserProfileMetadata.OS_VERSION, input);
		}
	}


	@Override
	public void setImpressions(List<ImpressionInterface> input) {
		if (input != null && !input.isEmpty()) {
			
		List<Record> list = Lists.newArrayList();
		for(ImpressionInterface elem : input){
			ImpressionRecord iRec = (ImpressionRecord) elem;
			Record<ImpressionMetadata> rec = (Record<ImpressionMetadata>) iRec.getBaseObject();
			list.add(rec);
		}
		this.value.setValue(AnalyticalUserProfileMetadata.IMPRESSIONS, list);

			
		}
	}


	@Override
	public void setIdMaps(IdMapInterface input) {
		if (input != null) {
			
		IdMapRecord iRec = (IdMapRecord) input;
		Record<IdMapMetadata> rec = (Record<IdMapMetadata>) iRec.getBaseObject();
		this.value.setValue(AnalyticalUserProfileMetadata.ID_MAPS, rec);

			
		}
	}


	@Override
	public void setTimeList(List<Integer> input) {
		if (input != null && !input.isEmpty()) {
			
		List<Integer> list = Lists.newArrayList();
		for(int elem : input){
			list.add(elem);
		}
		this.value.setValue(AnalyticalUserProfileMetadata.TIME_LIST, list);

			
		}
	}


    @Override
    public long getUserIdDefaultValue() {
        throw new NullPointerException("Require a valid value for user_id! Make sure the column has been selected!");
    }

    @Override
    public byte getVersionDefaultValue() {
        throw new NullPointerException("Require a valid value for version! Make sure the column has been selected!");
    }

    @Override
    public short getResolutionDefaultValue() {
        return (short)-1;
    }

    @Override
    public String getOsVersionDefaultValue() {
        return (String)"Unknown";
    }

    @Override
    public String getDayDefaultValue() {
        return (String)"Unknown";
    }


	@Override
	public int getImpressionsSize() {
		return this.value.getListSize(AnalyticalUserProfileMetadata.IMPRESSIONS);
	}

	@Override
	public int getTimeListSize() {
		return this.value.getListSize(AnalyticalUserProfileMetadata.TIME_LIST);
	}


	@Override
	public int compareTo(AnalyticalUserProfileInterface o) {
		return (o.getVersion() - this.getVersion());
	}

}

