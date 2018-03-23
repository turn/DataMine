package datamine.storage.recordbuffers.example.convertors;

import java.util.ArrayList;
import java.util.List;

import datamine.operator.UnaryOperatorInterface;
import datamine.storage.api.RecordBuilderInterface;
import datamine.storage.recordbuffers.example.interfaces.FirstLevelNestedTableInterface;
import datamine.storage.recordbuffers.example.interfaces.MainTableInterface;


/**
 * DO Not CHANGE! Auto-generated code
 */
public class MainTableInterfaceConvertor implements UnaryOperatorInterface<MainTableInterface, MainTableInterface> {

	private RecordBuilderInterface builder;

   public MainTableInterfaceConvertor(RecordBuilderInterface builder) {
   	this.builder = builder;
	}
	@Override
	public MainTableInterface apply(MainTableInterface input) {
		MainTableInterface output = builder.build(MainTableInterface.class);
		output.setLongRequiredColumn(input.getLongRequiredColumn());

		output.setIntSortedColumn(input.getIntSortedColumn());

		output.setByteColumn(input.getByteColumn());

		output.setBooleanColumn(input.isBooleanColumn());

		output.setShortColumn(input.getShortColumn());

		output.setFloatColumn(input.getFloatColumn());

		output.setDoubleColumn(input.getDoubleColumn());

		output.setStringColumn(input.getStringColumn());

		output.setBinaryColumn(input.getBinaryColumn());

		{
			List<FirstLevelNestedTableInterface> list = new ArrayList<FirstLevelNestedTableInterface>();
			FirstLevelNestedTableInterfaceConvertor convertor = new FirstLevelNestedTableInterfaceConvertor(builder);
			for (FirstLevelNestedTableInterface tuple: input.getNestedTableColumn()) {
				list.add(convertor.apply(tuple));
			}
			output.setNestedTableColumn(list);
		}
		{
			StructTableInterfaceConvertor convertor = new StructTableInterfaceConvertor(builder);
			if (input.getStructColumn() != null) {
       		output.setStructColumn(convertor.apply(input.getStructColumn()));
			}
		}
		output.setIntListColumn(input.getIntListColumn());


		return output;
	}

}

