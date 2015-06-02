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
public class ImpressionRecord implements ImpressionInterface {
    static final Logger LOG = LoggerFactory.getLogger(ImpressionRecord.class);

    Record<ImpressionMetadata> value = null;
	

    public ImpressionRecord() {
        value = new WritableRecord<ImpressionMetadata>(ImpressionMetadata.class);
    }

    public ImpressionRecord(Record<ImpressionMetadata> value) {
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
            this.value = (Record<ImpressionMetadata>) obj;
        }else{
            throw new IllegalArgumentException("Not Support type of "+obj.getClass());
        }
    }

    @Override
    public void referTo(BaseInterface right) {
        this.value = (Record<ImpressionMetadata>) right.getBaseObject();
    }

    @Override
    public void copyFrom(BaseInterface right) {
		// note that it may not be deep copy!!
		this.value = new WritableRecord<ImpressionMetadata>(ImpressionMetadata.class, 
			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));
    }

    @Override
    public boolean equals(Object that) {
        if (that == null){
            return false;
        }
        if (that instanceof ImpressionRecord){
            return this.getBaseObject().equals(((ImpressionRecord) that).getBaseObject());
        }
        return false;
    }

    @Override
    public int getMediaProviderId() {
        
        return this.value.getInt(ImpressionMetadata.MEDIA_PROVIDER_ID);
    }

    @Override
    public short getMpTptCategoryId() {
        
        return this.value.getShort(ImpressionMetadata.MP_TPT_CATEGORY_ID);
    }

    @Override
    public String getTruncatedUrl() {
        
        return this.value.getString(ImpressionMetadata.TRUNCATED_URL);
    }

    @Override
    public boolean isBid() {
        
        return this.value.getBool(ImpressionMetadata.BID);
    }

    @Override
    public byte getBidType() {
        
        return this.value.getByte(ImpressionMetadata.BID_TYPE);
    }

    @Override
    public List<AttributionResultInterface> getAttributionResults() {
            	List<AttributionResultInterface> dList = Lists.newArrayList();
		List<Object> sList = (List<Object>) this.value.getValue(ImpressionMetadata.ATTRIBUTION_RESULTS);
		if(sList != null) {
		   	for (Object cur : sList) {
    			dList.add(new AttributionResultRecord((Record)cur));
	    	}
		}
       return dList;

        
    }

    @Override
    public long getAllowedAdFormats() {
        
        return this.value.getLong(ImpressionMetadata.ALLOWED_AD_FORMATS);
    }

    @Override
    public double getCost() {
        
        return this.value.getDouble(ImpressionMetadata.COST);
    }



	@Override
	public void setMediaProviderId(int input) {
		if (1 == 1) {
			
			this.value.setValue(ImpressionMetadata.MEDIA_PROVIDER_ID, input);
		}
	}


	@Override
	public void setMpTptCategoryId(short input) {
		if (1 == 1) {
			
			this.value.setValue(ImpressionMetadata.MP_TPT_CATEGORY_ID, input);
		}
	}


	@Override
	public void setTruncatedUrl(String input) {
		if (1 == 1) {
			
			this.value.setValue(ImpressionMetadata.TRUNCATED_URL, input);
		}
	}


	@Override
	public void setBid(boolean input) {
		if (1 == 1) {
			
			this.value.setValue(ImpressionMetadata.BID, input);
		}
	}


	@Override
	public void setBidType(byte input) {
		if (1 == 1) {
			
			this.value.setValue(ImpressionMetadata.BID_TYPE, input);
		}
	}


	@Override
	public void setAttributionResults(List<AttributionResultInterface> input) {
		if (input != null && !input.isEmpty()) {
			
		List<Record> list = Lists.newArrayList();
		for(AttributionResultInterface elem : input){
			AttributionResultRecord iRec = (AttributionResultRecord) elem;
			Record<AttributionResultMetadata> rec = (Record<AttributionResultMetadata>) iRec.getBaseObject();
			list.add(rec);
		}
		this.value.setValue(ImpressionMetadata.ATTRIBUTION_RESULTS, list);

			
		}
	}


	@Override
	public void setAllowedAdFormats(long input) {
		if (1 == 1) {
			
			this.value.setValue(ImpressionMetadata.ALLOWED_AD_FORMATS, input);
		}
	}


	@Override
	public void setCost(double input) {
		if (1 == 1) {
			
			this.value.setValue(ImpressionMetadata.COST, input);
		}
	}


    @Override
    public int getMediaProviderIdDefaultValue() {
        throw new NullPointerException("Require a valid value for media_provider_id! Make sure the column has been selected!");
    }

    @Override
    public short getMpTptCategoryIdDefaultValue() {
        return (short)-1;
    }

    @Override
    public String getTruncatedUrlDefaultValue() {
        return (String)"Unknown";
    }

    @Override
    public boolean getBidDefaultValue() {
        return (boolean)true;
    }

    @Override
    public byte getBidTypeDefaultValue() {
        return (byte)-1;
    }

    @Override
    public long getAllowedAdFormatsDefaultValue() {
        return (long)-1;
    }

    @Override
    public double getCostDefaultValue() {
        return (double)0.0;
    }


	@Override
	public int getAttributionResultsSize() {
		return this.value.getListSize(ImpressionMetadata.ATTRIBUTION_RESULTS);
	}




}

