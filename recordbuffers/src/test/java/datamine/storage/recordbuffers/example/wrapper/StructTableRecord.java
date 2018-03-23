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
    public int getIntSortedColumn() {
        
        return this.value.getInt(StructTableMetadata.INT_SORTED_COLUMN);
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
	public void setIntSortedColumn(int input) {
		if (1 == 1) {
			
			this.value.setValue(StructTableMetadata.INT_SORTED_COLUMN, input);
		}
	}


    @Override
    public int getIntSortedColumnDefaultValue() {
        throw new NullPointerException("Require a valid value for int_sorted_column! Make sure the column has been selected!");
    }


	@Override
	public int getNestedTableColumnSize() {
		return this.value.getListSize(StructTableMetadata.NESTED_TABLE_COLUMN);
	}




}

