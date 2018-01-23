package datamine.storage.recordbuffers.example.printers;

import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.operator.UnaryOperatorInterface;


/**
 * DO Not CHANGE! Auto-generated code
 */
public class FirstLevelNestedTableInterfaceContentPrinter implements UnaryOperatorInterface<FirstLevelNestedTableInterface, String> {

	@Override
	public String apply(FirstLevelNestedTableInterface input) {
		StringBuffer out = new StringBuffer();
		out.append("{\n");
		out.append("event_time = ").append(input.getEventTime()).append("\n");
		out.append("int_required_column = ").append(input.getIntRequiredColumn()).append("\n");
		{
			SecondLevelNestedTableInterfaceContentPrinter printer = new SecondLevelNestedTableInterfaceContentPrinter();
			out.append("nested_table_column = ").append("[").append("\n");
			for (SecondLevelNestedTableInterface tuple: input.getNestedTableColumn()) {
				out.append(printer.apply(tuple)).append("\n");
			}
			out.append("]").append("\n");
		}
		out.append("string_derived_column = ").append(input.getStringDerivedColumn()).append("\n");

		out.append("}");
		return out.toString();
	}

}

