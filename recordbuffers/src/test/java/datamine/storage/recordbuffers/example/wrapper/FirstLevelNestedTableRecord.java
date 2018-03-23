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
 * DO Not CHANGE! Auto-generated code
 */
public class FirstLevelNestedTableRecord implements FirstLevelNestedTableInterface {
    static final Logger LOG = LoggerFactory.getLogger(FirstLevelNestedTableRecord.class);

    Record<FirstLevelNestedTableMetadata> value = null;
	
	private FirstLevelNestedTableDerivedValueInterface derivedFieldValues = new FirstLevelNestedTableDefaultDerivedValues();

	public FirstLevelNestedTableRecord(Record<FirstLevelNestedTableMetadata> record, FirstLevelNestedTableDerivedValueInterface derived) {
		value = record;
		derivedFieldValues = derived;
	}

	public void setDerivedValueImplementation(FirstLevelNestedTableDerivedValueInterface derived) {
		derivedFieldValues = derived;
	}



    public FirstLevelNestedTableRecord() {
        value = new WritableRecord<FirstLevelNestedTableMetadata>(FirstLevelNestedTableMetadata.class);
    }

    public FirstLevelNestedTableRecord(Record<FirstLevelNestedTableMetadata> value) {
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
            this.value = (Record<FirstLevelNestedTableMetadata>) obj;
        }else{
            throw new IllegalArgumentException("Not Support type of "+obj.getClass());
        }
    }

    @Override
    public void referTo(BaseInterface right) {
        this.value = (Record<FirstLevelNestedTableMetadata>) right.getBaseObject();
    }

    @Override
    public void copyFrom(BaseInterface right) {
		// note that it must be deep copy!!
		this.value = new WritableRecord<FirstLevelNestedTableMetadata>(FirstLevelNestedTableMetadata.class, 
			new RecordBuffer(((Record) right.getBaseObject()).getRecordBuffer()));
    }

    @Override
    public boolean equals(Object that) {
        if (that == null){
            return false;
        }
        if (that instanceof FirstLevelNestedTableRecord){
            return this.getBaseObject().equals(((FirstLevelNestedTableRecord) that).getBaseObject());
        }
        return false;
    }

    @Override
    public long getEventTime() {
        
        return this.value.getLong(FirstLevelNestedTableMetadata.EVENT_TIME);
    }

    @Override
    public int getIntRequiredColumn() {
        
        return this.value.getInt(FirstLevelNestedTableMetadata.INT_REQUIRED_COLUMN);
    }

    @Override
    public List<SecondLevelNestedTableInterface> getNestedTableColumn() {
            	List<SecondLevelNestedTableInterface> dList = Lists.newArrayList();
		List<Object> sList = (List<Object>) this.value.getValue(FirstLevelNestedTableMetadata.NESTED_TABLE_COLUMN);
		if(sList != null) {
		   	for (Object cur : sList) {
    			dList.add(new SecondLevelNestedTableRecord((Record)cur));
	    	}
		}
       return dList;

        
    }

    @Override
    public String getStringDerivedColumn() {
        
        return derivedFieldValues.getStringDerivedColumn();
    }



	@Override
	public void setEventTime(long input) {
		if (1 == 1) {
			
			this.value.setValue(FirstLevelNestedTableMetadata.EVENT_TIME, input);
		}
	}


	@Override
	public void setIntRequiredColumn(int input) {
		if (1 == 1) {
			
			this.value.setValue(FirstLevelNestedTableMetadata.INT_REQUIRED_COLUMN, input);
		}
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
		this.value.setValue(FirstLevelNestedTableMetadata.NESTED_TABLE_COLUMN, list);

			
		}
	}


    @Override
    public long getEventTimeDefaultValue() {
        throw new NullPointerException("Require a valid value for event_time! Make sure the column has been selected!");
    }

    @Override
    public int getIntRequiredColumnDefaultValue() {
        throw new NullPointerException("Require a valid value for int_required_column! Make sure the column has been selected!");
    }

    @Override
    public String getStringDerivedColumnDefaultValue() {
        return (String)"Unknown";
    }


	@Override
	public int getNestedTableColumnSize() {
		return this.value.getListSize(FirstLevelNestedTableMetadata.NESTED_TABLE_COLUMN);
	}


	@Override
	public int compareTo(FirstLevelNestedTableInterface o) {
		if (this == o) return 0;
		if (this == null) return -1;
		if (o == null) return 1;

		if (this.getEventTime() < o.getEventTime()) return -1;
		else if (this.getEventTime() > o.getEventTime()) return 1;
		else return 0;
	}


	public static class FirstLevelNestedTableDefaultDerivedValues implements FirstLevelNestedTableDerivedValueInterface {

	
	    @Override
    public String getStringDerivedColumn() {
        return (String)"Unknown";
    }


	}

}

