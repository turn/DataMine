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

import java.nio.ByteBuffer;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.base.Strings;

import datamine.storage.api.BaseInterface;
import datamine.storage.recordbuffers.*;
import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.storage.recordbuffers.example.model.*;



/**
 * DO NOT CHANGE! Auto-generated code
 */
public class ProviderUserIdRecord implements ProviderUserIdInterface {
    static final Logger LOG = LoggerFactory.getLogger(ProviderUserIdRecord.class);

    Record<ProviderUserIdMetadata> value = null;

    public ProviderUserIdRecord() {
        value = new Record<ProviderUserIdMetadata>(ProviderUserIdMetadata.class);
    }

    public ProviderUserIdRecord(Record<ProviderUserIdMetadata> value) {
        this.value = value;
    }

    public ProviderUserIdRecord(RecordBuffer buffer) {
        this.value = new Record<ProviderUserIdMetadata>(ProviderUserIdMetadata.class, buffer);
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
            this.value = (Record<ProviderUserIdMetadata>) obj;
        }else{
            throw new IllegalArgumentException("Not Support type of "+obj.getClass());
        }
    }

    @Override
    public void referTo(BaseInterface right) {
        this.value = (Record<ProviderUserIdMetadata>) right.getBaseObject();
    }

    @Override
    public void copyFrom(BaseInterface right) {
		// note that it may not be deep copy!!
		this.value = new Record<ProviderUserIdMetadata>(ProviderUserIdMetadata.class, 
			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));
    }

    @Override
    public boolean equals(Object that) {
        if (that == null){
            return false;
        }
        if (that instanceof ProviderUserIdRecord){
            return this.getBaseObject().equals(((ProviderUserIdRecord) that).getBaseObject());
        }
        return false;
    }

    @Override
    public byte getProviderType() {
        
        return (Byte) this.value.getValue(ProviderUserIdMetadata.PROVIDER_TYPE);
    }

    @Override
    public int getProviderId() {
        
        return (Integer) this.value.getValue(ProviderUserIdMetadata.PROVIDER_ID);
    }



	@Override
	public void setProviderType(byte input) {
		if (1 == 1) {
			
			this.value.setValue(ProviderUserIdMetadata.PROVIDER_TYPE, input);
		}
	}


	@Override
	public void setProviderId(int input) {
		if (1 == 1) {
			
			this.value.setValue(ProviderUserIdMetadata.PROVIDER_ID, input);
		}
	}


    @Override
    public byte getProviderTypeDefaultValue() {
        throw new NullPointerException("Require a valid value for provider_type! Make sure the column has been selected!");
    }

    @Override
    public int getProviderIdDefaultValue() {
        throw new NullPointerException("Require a valid value for provider_id! Make sure the column has been selected!");
    }




}

