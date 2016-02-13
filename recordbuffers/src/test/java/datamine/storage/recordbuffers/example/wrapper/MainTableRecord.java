/**
 * Copyright (C) 2016 Turn Inc. (yan.qi@turn.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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
public class MainTableRecord implements MainTableInterface {
    static final Logger LOG = LoggerFactory.getLogger(MainTableRecord.class);

    Record<MainTableMetadata> value = null;
	
	private MainTableDerivedValueInterface derivedFieldValues = new MainTableDefaultDerivedValues();

	public MainTableRecord(Record<MainTableMetadata> record, MainTableDerivedValueInterface derived) {
		value = record;
		derivedFieldValues = derived;
	}

	public void setDerivedValueImplementation(MainTableDerivedValueInterface derived) {
		derivedFieldValues = derived;
	}



    public MainTableRecord() {
        value = new WritableRecord<MainTableMetadata>(MainTableMetadata.class);
    }

    public MainTableRecord(Record<MainTableMetadata> value) {
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
            this.value = (Record<MainTableMetadata>) obj;
        }else{
            throw new IllegalArgumentException("Not Support type of "+obj.getClass());
        }
    }

    @Override
    public void referTo(BaseInterface right) {
        this.value = (Record<MainTableMetadata>) right.getBaseObject();
    }

    @Override
    public void copyFrom(BaseInterface right) {
		// note that it must be deep copy!!
		this.value = new WritableRecord<MainTableMetadata>(MainTableMetadata.class, 
			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));
    }

    @Override
    public boolean equals(Object that) {
        if (that == null){
            return false;
        }
        if (that instanceof MainTableRecord){
            return this.getBaseObject().equals(((MainTableRecord) that).getBaseObject());
        }
        return false;
    }

    @Override
    public long getLongRequiredColumn() {
        
        return this.value.getLong(MainTableMetadata.LONG_REQUIRED_COLUMN);
    }

    @Override
    public int getIntSortedColumn() {
        
        return this.value.getInt(MainTableMetadata.INT_SORTED_COLUMN);
    }

    @Override
    public byte getByteColumn() {
        
        return this.value.getByte(MainTableMetadata.BYTE_COLUMN);
    }

    @Override
    public boolean isBooleanColumn() {
        
        return this.value.getBool(MainTableMetadata.BOOLEAN_COLUMN);
    }

    @Override
    public short getShortColumn() {
        
        return this.value.getShort(MainTableMetadata.SHORT_COLUMN);
    }

    @Override
    public float getFloatColumn() {
        
        return this.value.getFloat(MainTableMetadata.FLOAT_COLUMN);
    }

    @Override
    public double getDoubleColumn() {
        
        return this.value.getDouble(MainTableMetadata.DOUBLE_COLUMN);
    }

    @Override
    public String getStringColumn() {
        
        return this.value.getString(MainTableMetadata.STRING_COLUMN);
    }

    @Override
    public byte[] getBinaryColumn() {
        
        return this.value.getBinary(MainTableMetadata.BINARY_COLUMN);
    }

    @Override
    public List<FirstLevelNestedTableInterface> getNestedTableColumn() {
            	List<FirstLevelNestedTableInterface> dList = Lists.newArrayList();
		List<Object> sList = (List<Object>) this.value.getValue(MainTableMetadata.NESTED_TABLE_COLUMN);
		if(sList != null) {
		   	for (Object cur : sList) {
    			dList.add(new FirstLevelNestedTableRecord((Record)cur));
	    	}
		}
       return dList;

        
    }

    @Override
    public StructTableInterface getStructColumn() {
        
        
		Record record = (Record) this.value.getValue(MainTableMetadata.STRUCT_COLUMN);
		if (record == null) {
			return null;
		} else {
			return new StructTableRecord(record);
		}

    }

    @Override
    public List<Integer> getIntListColumn() {
            	List<Integer> dList = Lists.newArrayList();
		List<Object> sList = (List<Object>) this.value.getValue(MainTableMetadata.INT_LIST_COLUMN);
		if(sList != null) {
		   	for (Object cur : sList) {
    			dList.add((Integer) cur);
	    	}
		}
       return dList;

        
    }

    @Override
    public String getStringDerivedColumn() {
        
        return derivedFieldValues.getStringDerivedColumn();
    }

    @Override
    public int getIntDerivedColumn() {
        
        return derivedFieldValues.getIntDerivedColumn();
    }



	@Override
	public void setLongRequiredColumn(long input) {
		if (1 == 1) {
			
			this.value.setValue(MainTableMetadata.LONG_REQUIRED_COLUMN, input);
		}
	}


	@Override
	public void setIntSortedColumn(int input) {
		if (1 == 1) {
			
			this.value.setValue(MainTableMetadata.INT_SORTED_COLUMN, input);
		}
	}


	@Override
	public void setByteColumn(byte input) {
		if (1 == 1) {
			
			this.value.setValue(MainTableMetadata.BYTE_COLUMN, input);
		}
	}


	@Override
	public void setBooleanColumn(boolean input) {
		if (1 == 1) {
			
			this.value.setValue(MainTableMetadata.BOOLEAN_COLUMN, input);
		}
	}


	@Override
	public void setShortColumn(short input) {
		if (1 == 1) {
			
			this.value.setValue(MainTableMetadata.SHORT_COLUMN, input);
		}
	}


	@Override
	public void setFloatColumn(float input) {
		if (1 == 1) {
			
			this.value.setValue(MainTableMetadata.FLOAT_COLUMN, input);
		}
	}


	@Override
	public void setDoubleColumn(double input) {
		if (1 == 1) {
			
			this.value.setValue(MainTableMetadata.DOUBLE_COLUMN, input);
		}
	}


	@Override
	public void setStringColumn(String input) {
		if (1 == 1) {
			
			this.value.setValue(MainTableMetadata.STRING_COLUMN, input);
		}
	}


	@Override
	public void setBinaryColumn(byte[] input) {
		if (1 == 1) {
			
			this.value.setValue(MainTableMetadata.BINARY_COLUMN, input);
		}
	}


	@Override
	public void setNestedTableColumn(List<FirstLevelNestedTableInterface> input) {
		if (input != null && !input.isEmpty()) {
			
		List<Record> list = Lists.newArrayList();
		for(FirstLevelNestedTableInterface elem : input){
			FirstLevelNestedTableRecord iRec = (FirstLevelNestedTableRecord) elem;
			Record<FirstLevelNestedTableMetadata> rec = (Record<FirstLevelNestedTableMetadata>) iRec.getBaseObject();
			list.add(rec);
		}
		this.value.setValue(MainTableMetadata.NESTED_TABLE_COLUMN, list);

			
		}
	}


	@Override
	public void setStructColumn(StructTableInterface input) {
		if (input != null) {
			
		StructTableRecord iRec = (StructTableRecord) input;
		Record<StructTableMetadata> rec = (Record<StructTableMetadata>) iRec.getBaseObject();
		this.value.setValue(MainTableMetadata.STRUCT_COLUMN, rec);

			
		}
	}


	@Override
	public void setIntListColumn(List<Integer> input) {
		if (input != null && !input.isEmpty()) {
			
		List<Integer> list = Lists.newArrayList();
		for(int elem : input){
			list.add(elem);
		}
		this.value.setValue(MainTableMetadata.INT_LIST_COLUMN, list);

			
		}
	}


    @Override
    public long getLongRequiredColumnDefaultValue() {
        throw new NullPointerException("Require a valid value for long_required_column! Make sure the column has been selected!");
    }

    @Override
    public int getIntSortedColumnDefaultValue() {
        throw new NullPointerException("Require a valid value for int_sorted_column! Make sure the column has been selected!");
    }

    @Override
    public byte getByteColumnDefaultValue() {
        return (byte)-1;
    }

    @Override
    public boolean getBooleanColumnDefaultValue() {
        return (boolean)false;
    }

    @Override
    public short getShortColumnDefaultValue() {
        return (short)0;
    }

    @Override
    public float getFloatColumnDefaultValue() {
        return (float)0.0;
    }

    @Override
    public double getDoubleColumnDefaultValue() {
        return (double)0.001;
    }

    @Override
    public String getStringColumnDefaultValue() {
        return (String)"Unknown";
    }

    @Override
    public String getStringDerivedColumnDefaultValue() {
        return (String)"Unknown";
    }

    @Override
    public int getIntDerivedColumnDefaultValue() {
        return (int)0;
    }


	@Override
	public int getNestedTableColumnSize() {
		return this.value.getListSize(MainTableMetadata.NESTED_TABLE_COLUMN);
	}

	@Override
	public int getIntListColumnSize() {
		return this.value.getListSize(MainTableMetadata.INT_LIST_COLUMN);
	}


	@Override
	public int compareTo(MainTableInterface o) {
		return (o.getIntSortedColumn() - this.getIntSortedColumn());
	}


	public static class MainTableDefaultDerivedValues implements MainTableDerivedValueInterface {

	
	    @Override
    public String getStringDerivedColumn() {
        return (String)"Unknown";
    }

    @Override
    public int getIntDerivedColumn() {
        return (int)0;
    }


	}

}

