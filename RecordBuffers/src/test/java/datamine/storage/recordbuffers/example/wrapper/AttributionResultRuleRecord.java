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
public class AttributionResultRuleRecord implements AttributionResultRuleInterface {
    static final Logger LOG = LoggerFactory.getLogger(AttributionResultRuleRecord.class);

    Record<AttributionResultRuleMetadata> value = null;
	

    public AttributionResultRuleRecord() {
        value = new WritableRecord<AttributionResultRuleMetadata>(AttributionResultRuleMetadata.class);
    }

    public AttributionResultRuleRecord(Record<AttributionResultRuleMetadata> value) {
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
            this.value = (Record<AttributionResultRuleMetadata>) obj;
        }else{
            throw new IllegalArgumentException("Not Support type of "+obj.getClass());
        }
    }

    @Override
    public void referTo(BaseInterface right) {
        this.value = (Record<AttributionResultRuleMetadata>) right.getBaseObject();
    }

    @Override
    public void copyFrom(BaseInterface right) {
		// note that it must be deep copy!!
		this.value = new WritableRecord<AttributionResultRuleMetadata>(AttributionResultRuleMetadata.class, 
			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));
    }

    @Override
    public boolean equals(Object that) {
        if (that == null){
            return false;
        }
        if (that instanceof AttributionResultRuleRecord){
            return this.getBaseObject().equals(((AttributionResultRuleRecord) that).getBaseObject());
        }
        return false;
    }

    @Override
    public byte getRunNum() {
        
        return this.value.getByte(AttributionResultRuleMetadata.RUN_NUM);
    }

    @Override
    public String getValue() {
        
        return this.value.getString(AttributionResultRuleMetadata.VALUE);
    }



	@Override
	public void setRunNum(byte input) {
		if (1 == 1) {
			
			this.value.setValue(AttributionResultRuleMetadata.RUN_NUM, input);
		}
	}


	@Override
	public void setValue(String input) {
		if (1 == 1) {
			
			this.value.setValue(AttributionResultRuleMetadata.VALUE, input);
		}
	}


    @Override
    public byte getRunNumDefaultValue() {
        throw new NullPointerException("Require a valid value for run_num! Make sure the column has been selected!");
    }

    @Override
    public String getValueDefaultValue() {
        return (String)"Unknown";
    }





}

