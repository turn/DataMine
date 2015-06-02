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
public class AttributionResultRecord implements AttributionResultInterface {
    static final Logger LOG = LoggerFactory.getLogger(AttributionResultRecord.class);

    Record<AttributionResultMetadata> value = null;
	

    public AttributionResultRecord() {
        value = new WritableRecord<AttributionResultMetadata>(AttributionResultMetadata.class);
    }

    public AttributionResultRecord(Record<AttributionResultMetadata> value) {
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
            this.value = (Record<AttributionResultMetadata>) obj;
        }else{
            throw new IllegalArgumentException("Not Support type of "+obj.getClass());
        }
    }

    @Override
    public void referTo(BaseInterface right) {
        this.value = (Record<AttributionResultMetadata>) right.getBaseObject();
    }

    @Override
    public void copyFrom(BaseInterface right) {
		// note that it may not be deep copy!!
		this.value = new WritableRecord<AttributionResultMetadata>(AttributionResultMetadata.class, 
			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));
    }

    @Override
    public boolean equals(Object that) {
        if (that == null){
            return false;
        }
        if (that instanceof AttributionResultRecord){
            return this.getBaseObject().equals(((AttributionResultRecord) that).getBaseObject());
        }
        return false;
    }

    @Override
    public int getId() {
        
        return this.value.getInt(AttributionResultMetadata.ID);
    }

    @Override
    public List<AttributionResultRuleInterface> getRules() {
            	List<AttributionResultRuleInterface> dList = Lists.newArrayList();
		List<Object> sList = (List<Object>) this.value.getValue(AttributionResultMetadata.RULES);
		if(sList != null) {
		   	for (Object cur : sList) {
    			dList.add(new AttributionResultRuleRecord((Record)cur));
	    	}
		}
       return dList;

        
    }



	@Override
	public void setId(int input) {
		if (1 == 1) {
			
			this.value.setValue(AttributionResultMetadata.ID, input);
		}
	}


	@Override
	public void setRules(List<AttributionResultRuleInterface> input) {
		if (input != null && !input.isEmpty()) {
			
		List<Record> list = Lists.newArrayList();
		for(AttributionResultRuleInterface elem : input){
			AttributionResultRuleRecord iRec = (AttributionResultRuleRecord) elem;
			Record<AttributionResultRuleMetadata> rec = (Record<AttributionResultRuleMetadata>) iRec.getBaseObject();
			list.add(rec);
		}
		this.value.setValue(AttributionResultMetadata.RULES, list);

			
		}
	}


    @Override
    public int getIdDefaultValue() {
        throw new NullPointerException("Require a valid value for id! Make sure the column has been selected!");
    }


	@Override
	public int getRulesSize() {
		return this.value.getListSize(AttributionResultMetadata.RULES);
	}




}

