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
public class StructTableRecord implements StructTableInterface {
    static final Logger LOG = LoggerFactory.getLogger(StructTableRecord.class);

    Record<StructTableMetadata> value = null;
	

    public StructTableRecord() {
        value = new WritableRecord<StructTableMetadata>(StructTableMetadata.class);
    }

    public StructTableRecord(Record<StructTableMetadata> value) {
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
            this.value = (Record<StructTableMetadata>) obj;
        }else{
            throw new IllegalArgumentException("Not Support type of "+obj.getClass());
        }
    }

    @Override
    public void referTo(BaseInterface right) {
        this.value = (Record<StructTableMetadata>) right.getBaseObject();
    }

    @Override
    public void copyFrom(BaseInterface right) {
		// note that it must be deep copy!!
		this.value = new WritableRecord<StructTableMetadata>(StructTableMetadata.class, 
			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));
    }

    @Override
    public boolean equals(Object that) {
        if (that == null){
            return false;
        }
        if (that instanceof StructTableRecord){
            return this.getBaseObject().equals(((StructTableRecord) that).getBaseObject());
        }
        return false;
    }

    @Override
    public List<SecondLevelNestedTableInterface> getNestedTableColumn() {
            	List<SecondLevelNestedTableInterface> dList = Lists.newArrayList();
		List<Object> sList = (List<Object>) this.value.getValue(StructTableMetadata.NESTED_TABLE_COLUMN);
		if(sList != null) {
		   	for (Object cur : sList) {
    			dList.add(new SecondLevelNestedTableRecord((Record)cur));
	    	}
		}
       return dList;

        
    }



	@Override
	public void setNestedTableColumn(List<SecondLevelNestedTableInterface> input) {
		if (input != null && !input.isEmpty()) {
			
		List<Record> list = Lists.newArrayList();
		for(SecondLevelNestedTableInterface elem : input){
			SecondLevelNestedTableRecord iRec = (SecondLevelNestedTableRecord) elem;
			Record<SecondLevelNestedTableMetadata> rec = (Record<SecondLevelNestedTableMetadata>) iRec.getBaseObject();
			list.add(rec);
		}
		this.value.setValue(StructTableMetadata.NESTED_TABLE_COLUMN, list);

			
		}
	}



	@Override
	public int getNestedTableColumnSize() {
		return this.value.getListSize(StructTableMetadata.NESTED_TABLE_COLUMN);
	}




}

