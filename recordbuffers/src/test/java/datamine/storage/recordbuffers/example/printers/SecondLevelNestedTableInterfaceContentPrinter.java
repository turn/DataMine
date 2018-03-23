package datamine.storage.recordbuffers.example.printers;

import datamine.storage.recordbuffers.example.interfaces.*;
import datamine.operator.UnaryOperatorInterface;


/**
 * DO Not CHANGE! Auto-generated code
 */
public class SecondLevelNestedTableInterfaceContentPrinter implements UnaryOperatorInterface<SecondLevelNestedTableInterface, String> {

	@Override
	public String apply(SecondLevelNestedTableInterface input) {
		StringBuffer out = new StringBuffer();
		out.append("{\n");
		out.append("byte_required_column = ").append(input.getByteRequiredColumn()).append("\n");
		out.append("boolean_list_column = ").append(input.getBooleanListColumn()).append("\n");

		out.append("}");
		return out.toString();
	}

}

