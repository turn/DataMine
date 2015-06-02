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
public class IdMapRecord implements IdMapInterface {
    static final Logger LOG = LoggerFactory.getLogger(IdMapRecord.class);

    Record<IdMapMetadata> value = null;
	

    public IdMapRecord() {
        value = new WritableRecord<IdMapMetadata>(IdMapMetadata.class);
    }

    public IdMapRecord(Record<IdMapMetadata> value) {
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
            this.value = (Record<IdMapMetadata>) obj;
        }else{
            throw new IllegalArgumentException("Not Support type of "+obj.getClass());
        }
    }

    @Override
    public void referTo(BaseInterface right) {
        this.value = (Record<IdMapMetadata>) right.getBaseObject();
    }

    @Override
    public void copyFrom(BaseInterface right) {
		// note that it may not be deep copy!!
		this.value = new WritableRecord<IdMapMetadata>(IdMapMetadata.class, 
			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));
    }

    @Override
    public boolean equals(Object that) {
        if (that == null){
            return false;
        }
        if (that instanceof IdMapRecord){
            return this.getBaseObject().equals(((IdMapRecord) that).getBaseObject());
        }
        return false;
    }

    @Override
    public List<ProviderUserIdInterface> getMediaProviderIds() {
            	List<ProviderUserIdInterface> dList = Lists.newArrayList();
		List<Object> sList = (List<Object>) this.value.getValue(IdMapMetadata.MEDIA_PROVIDER_IDS);
		if(sList != null) {
		   	for (Object cur : sList) {
    			dList.add(new ProviderUserIdRecord((Record)cur));
	    	}
		}
       return dList;

        
    }



	@Override
	public void setMediaProviderIds(List<ProviderUserIdInterface> input) {
		if (input != null && !input.isEmpty()) {
			
		List<Record> list = Lists.newArrayList();
		for(ProviderUserIdInterface elem : input){
			ProviderUserIdRecord iRec = (ProviderUserIdRecord) elem;
			Record<ProviderUserIdMetadata> rec = (Record<ProviderUserIdMetadata>) iRec.getBaseObject();
			list.add(rec);
		}
		this.value.setValue(IdMapMetadata.MEDIA_PROVIDER_IDS, list);

			
		}
	}



	@Override
	public int getMediaProviderIdsSize() {
		return this.value.getListSize(IdMapMetadata.MEDIA_PROVIDER_IDS);
	}




}

