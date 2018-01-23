package datamine.storage.recordbuffers.example.printers;

import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.operator.UnaryOperatorInterface;


/**
 * DO Not CHANGE! Auto-generated code
 */
public class StructTableInterfaceContentPrinter implements UnaryOperatorInterface<StructTableInterface, String> {

	@Override
	public String apply(StructTableInterface input) {
		StringBuffer out = new StringBuffer();
		out.append("{\n");
		{
			SecondLevelNestedTableInterfaceContentPrinter printer = new SecondLevelNestedTableInterfaceContentPrinter();
			out.append("nested_table_column = ").append("[").append("\n");
			for (SecondLevelNestedTableInterface tuple: input.getNestedTableColumn()) {
				out.append(printer.apply(tuple)).append("\n");
			}
			out.append("]").append("\n");
		}
		out.append("int_sorted_column = ").append(input.getIntSortedColumn()).append("\n");

		out.append("}");
		return out.toString();
	}

}

