package datamine.storage.recordbuffers.example.printers;

import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.operator.UnaryOperatorInterface;


/**
 * DO Not CHANGE! Auto-generated code
 */
public class MainTableInterfaceContentPrinter implements UnaryOperatorInterface<MainTableInterface, String> {

	@Override
	public String apply(MainTableInterface input) {
		StringBuffer out = new StringBuffer();
		out.append("{\n");
		out.append("long_required_column = ").append(input.getLongRequiredColumn()).append("\n");
		out.append("int_sorted_column = ").append(input.getIntSortedColumn()).append("\n");
		out.append("byte_column = ").append(input.getByteColumn()).append("\n");
		out.append("boolean_column = ").append(input.isBooleanColumn()).append("\n");
		out.append("short_column = ").append(input.getShortColumn()).append("\n");
		out.append("float_column = ").append(input.getFloatColumn()).append("\n");
		out.append("double_column = ").append(input.getDoubleColumn()).append("\n");
		out.append("string_column = ").append(input.getStringColumn()).append("\n");
		out.append("binary_column = ").append(input.getBinaryColumn()).append("\n");
		{
			FirstLevelNestedTableInterfaceContentPrinter printer = new FirstLevelNestedTableInterfaceContentPrinter();
			out.append("nested_table_column = ").append("[").append("\n");
			for (FirstLevelNestedTableInterface tuple: input.getNestedTableColumn()) {
				out.append(printer.apply(tuple)).append("\n");
			}
			out.append("]").append("\n");
		}
		{
			StructTableInterfaceContentPrinter printer = new StructTableInterfaceContentPrinter();
			if (input.getStructColumn() != null) {
       		out.append("struct_column = ").append(printer.apply(input.getStructColumn())).append("\n");
			}
		}
		out.append("int_list_column = ").append(input.getIntListColumn()).append("\n");
		out.append("string_derived_column = ").append(input.getStringDerivedColumn()).append("\n");
		out.append("int_derived_column = ").append(input.getIntDerivedColumn()).append("\n");

		out.append("}");
		return out.toString();
	}

}

