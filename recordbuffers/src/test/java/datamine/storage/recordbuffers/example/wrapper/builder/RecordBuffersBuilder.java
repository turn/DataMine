package datamine.storage.recordbuffers.example.wrapper.builder;


import datamine.storage.api.BaseInterface;
import datamine.storage.api.RecordBuilderInterface;
import datamine.storage.recordbuffers.example.wrapper.*;
import datamine.storage.recordbuffers.example.interfaces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * DO Not CHANGE! Auto-generated code
 */
public class RecordBuffersBuilder implements RecordBuilderInterface {

	static final Logger LOG = LoggerFactory.getLogger(RecordBuffersBuilder.class);

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BaseInterface> T build(Class<T> tableClass) {

		try {
			
		if (tableClass == MainTableInterface.class) {
			return (T) MainTableRecord.class.newInstance();
		}
		else
		if (tableClass == SecondLevelNestedTableInterface.class) {
			return (T) SecondLevelNestedTableRecord.class.newInstance();
		}
		else
		if (tableClass == FirstLevelNestedTableInterface.class) {
			return (T) FirstLevelNestedTableRecord.class.newInstance();
		}
		else
		if (tableClass == StructTableInterface.class) {
			return (T) StructTableRecord.class.newInstance();
		}

		} catch (InstantiationException e) {
			LOG.error("The object can not be created for " + tableClass + ":" + e);
		} catch (IllegalAccessException e) {
			LOG.error("The object can not be created for " + tableClass + ":" + e);
		}

		LOG.error("Cannot create an instance for "+tableClass);
		throw new IllegalArgumentException("Not support for the record of "+tableClass);
	}
}


