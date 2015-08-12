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
public class SecondLevelNestedTableRecord implements SecondLevelNestedTableInterface {
    static final Logger LOG = LoggerFactory.getLogger(SecondLevelNestedTableRecord.class);

    Record<SecondLevelNestedTableMetadata> value = null;
	

    public SecondLevelNestedTableRecord() {
        value = new WritableRecord<SecondLevelNestedTableMetadata>(SecondLevelNestedTableMetadata.class);
    }

    public SecondLevelNestedTableRecord(Record<SecondLevelNestedTableMetadata> value) {
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
            this.value = (Record<SecondLevelNestedTableMetadata>) obj;
        }else{
            throw new IllegalArgumentException("Not Support type of "+obj.getClass());
        }
    }

    @Override
    public void referTo(BaseInterface right) {
        this.value = (Record<SecondLevelNestedTableMetadata>) right.getBaseObject();
    }

    @Override
    public void copyFrom(BaseInterface right) {
		// note that it must be deep copy!!
		this.value = new WritableRecord<SecondLevelNestedTableMetadata>(SecondLevelNestedTableMetadata.class, 
			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));
    }

    @Override
    public boolean equals(Object that) {
        if (that == null){
            return false;
        }
        if (that instanceof SecondLevelNestedTableRecord){
            return this.getBaseObject().equals(((SecondLevelNestedTableRecord) that).getBaseObject());
        }
        return false;
    }

    @Override
    public byte getByteRequiredColumn() {
        
        return this.value.getByte(SecondLevelNestedTableMetadata.BYTE_REQUIRED_COLUMN);
    }

    @Override
    public List<Boolean> getBooleanListColumn() {
            	List<Boolean> dList = Lists.newArrayList();
		List<Object> sList = (List<Object>) this.value.getValue(SecondLevelNestedTableMetadata.BOOLEAN_LIST_COLUMN);
		if(sList != null) {
		   	for (Object cur : sList) {
    			dList.add((Boolean) cur);
	    	}
		}
       return dList;

        
    }



	@Override
	public void setByteRequiredColumn(byte input) {
		if (1 == 1) {
			
			this.value.setValue(SecondLevelNestedTableMetadata.BYTE_REQUIRED_COLUMN, input);
		}
	}


	@Override
	public void setBooleanListColumn(List<Boolean> input) {
		if (input != null && !input.isEmpty()) {
			
		List<Boolean> list = Lists.newArrayList();
		for(boolean elem : input){
			list.add(elem);
		}
		this.value.setValue(SecondLevelNestedTableMetadata.BOOLEAN_LIST_COLUMN, list);

			
		}
	}


    @Override
    public byte getByteRequiredColumnDefaultValue() {
        throw new NullPointerException("Require a valid value for byte_required_column! Make sure the column has been selected!");
    }


	@Override
	public int getBooleanListColumnSize() {
		return this.value.getListSize(SecondLevelNestedTableMetadata.BOOLEAN_LIST_COLUMN);
	}




}

