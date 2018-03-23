package datamine.storage.recordbuffers.example.convertors;

import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.operator.UnaryOperatorInterface;
import datamine.storage.api.RecordBuilderInterface;
import java.util.*;


/**
 * DO Not CHANGE! Auto-generated code
 */
public class SecondLevelNestedTableInterfaceConvertor implements UnaryOperatorInterface<SecondLevelNestedTableInterface, SecondLevelNestedTableInterface> {

	private RecordBuilderInterface builder;

   public SecondLevelNestedTableInterfaceConvertor(RecordBuilderInterface builder) {
   	this.builder = builder;
	}
	@Override
	public SecondLevelNestedTableInterface apply(SecondLevelNestedTableInterface input) {
		SecondLevelNestedTableInterface output = builder.build(SecondLevelNestedTableInterface.class);
		output.setByteRequiredColumn(input.getByteRequiredColumn());

		output.setBooleanListColumn(input.getBooleanListColumn());


		return output;
	}

}

