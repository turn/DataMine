package datamine.storage.recordbuffers.example.convertors;

import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.operator.UnaryOperatorInterface;
import datamine.storage.api.RecordBuilderInterface;
import java.util.*;


/**
 * DO Not CHANGE! Auto-generated code
 */
public class FirstLevelNestedTableInterfaceConvertor implements UnaryOperatorInterface<FirstLevelNestedTableInterface, FirstLevelNestedTableInterface> {

	private RecordBuilderInterface builder;

   public FirstLevelNestedTableInterfaceConvertor(RecordBuilderInterface builder) {
   	this.builder = builder;
	}
	@Override
	public FirstLevelNestedTableInterface apply(FirstLevelNestedTableInterface input) {
		FirstLevelNestedTableInterface output = builder.build(FirstLevelNestedTableInterface.class);
		output.setEventTime(input.getEventTime());

		output.setIntRequiredColumn(input.getIntRequiredColumn());

		{
			List<SecondLevelNestedTableInterface> list = new ArrayList<SecondLevelNestedTableInterface>();
			SecondLevelNestedTableInterfaceConvertor convertor = new SecondLevelNestedTableInterfaceConvertor(builder);
			for (SecondLevelNestedTableInterface tuple: input.getNestedTableColumn()) {
				list.add(convertor.apply(tuple));
			}
			output.setNestedTableColumn(list);
		}

		return output;
	}

}

