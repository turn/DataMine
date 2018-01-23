package datamine.storage.recordbuffers.example.convertors;

import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.operator.UnaryOperatorInterface;
import datamine.storage.api.RecordBuilderInterface;
import java.util.*;


/**
 * DO Not CHANGE! Auto-generated code
 */
public class StructTableInterfaceConvertor implements UnaryOperatorInterface<StructTableInterface, StructTableInterface> {

	private RecordBuilderInterface builder;

   public StructTableInterfaceConvertor(RecordBuilderInterface builder) {
   	this.builder = builder;
	}
	@Override
	public StructTableInterface apply(StructTableInterface input) {
		StructTableInterface output = builder.build(StructTableInterface.class);
		{
			List<SecondLevelNestedTableInterface> list = new ArrayList<SecondLevelNestedTableInterface>();
			SecondLevelNestedTableInterfaceConvertor convertor = new SecondLevelNestedTableInterfaceConvertor(builder);
			for (SecondLevelNestedTableInterface tuple: input.getNestedTableColumn()) {
				list.add(convertor.apply(tuple));
			}
			output.setNestedTableColumn(list);
		}
		output.setIntSortedColumn(input.getIntSortedColumn());


		return output;
	}

}

