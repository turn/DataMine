package datamine.query.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static datamine.query.data.ValueUtils.ArrayLongDataType;
import static datamine.query.data.ValueUtils.ArrayStringDataType;
import static datamine.query.data.ValueUtils.BooleanDataType;
import static datamine.query.data.ValueUtils.DoubleDataType;
import static datamine.query.data.ValueUtils.FloatDataType;
import static datamine.query.data.ValueUtils.ListFloatDataType;
import static datamine.query.data.ValueUtils.ListLongDataType;
import static datamine.query.data.ValueUtils.ListStringDataType;
import static datamine.query.data.ValueUtils.LongDataType;
import static datamine.query.data.ValueUtils.NullDataType;
import static datamine.query.data.ValueUtils.StringDataType;
import static datamine.query.data.ValueUtils.UnknownType;

/**
 * Value is the very basic concept in the profile analytics. It is a representation of values
 * in the query and its execution.
 */
public class Value implements Serializable {

    private static final String NUMERIC_VALUE_DELIMITER = ",";
    private static final String STRING_VALUE_DELIMITER = "``";

    private int type = UnknownType;
    private Object data = null;

    public Value() {}

    public Value(long value) {
        data = value;
        type = LongDataType;
    }

    public Value(int value) {
        data = value;
        type = LongDataType;
    }

    public Value(short value) {
        data = value;
        type = LongDataType;
    }

    public Value(byte value) {
        data = value;
        type = LongDataType;
    }

    public Value(float value) {
        data = value;
        type = DoubleDataType;
    }

    public Value(double value) {
        data = value;
        type = DoubleDataType;
    }

    public Value(String value) {
        data = value;
        type = StringDataType;
    }

    public Value(boolean value) {
        data = value;
        type = BooleanDataType;
    }

    public Value(long[] value) {
        data = value;
        type = ArrayLongDataType;
    }

    public Value(String[] value) {
        data = value;
        type = ArrayStringDataType;
    }

    /**
     * Check the first element of the input list to decide the data type.
     *
     * @param value a list of values with the same data type, e.g., float or string;
     *              the first element of the list determines the type of the entire list.
     */
    public Value(List value) {
        if (value != null && !value.isEmpty()) {
            data = value;
            Object element = value.get(0);
            if (element instanceof Long || element instanceof Integer
                || element instanceof Short || element instanceof Byte) {
                type = ListLongDataType;
            } else if (element instanceof Float || element instanceof Double) {
                type = ListFloatDataType;
            } else if (element instanceof String) {
                type = ListStringDataType;
            } else {
                throw new IllegalArgumentException(String.format(
                    "Not a supported LIST type: long, float or string: %s", value));
            }
        } else {
            throw new IllegalArgumentException(
                String.format("Null or empty LIST: %s", value));
        }
    }

    public Value(Object value, int type) {
        if (value == null) {
            throw new IllegalArgumentException("Value can not be null");
        }
        this.data = value;
        this.type = type;
    }

    public Value(String value, int type) {

        if (value == null) {
            throw new IllegalArgumentException("Value can not be null");
        }

        this.type = type;
        if (this.type == LongDataType) {
            data = Long.parseLong(value);
        } else if (this.type == DoubleDataType || this.type == FloatDataType) {
            data = Double.parseDouble(value);
        } else if (this.type == BooleanDataType) {
            data = Boolean.parseBoolean(value);
        } else if (this.type == StringDataType) {
            data = value;
        } else if (this.type == NullDataType) {
            data = value;
        } else if (this.type == ArrayLongDataType) {
            // remove the first and last brackets, (, and )
            String[] valStrs =
                value.substring(1, value.length() - 1).split(NUMERIC_VALUE_DELIMITER);
            long[] vals = new long[valStrs.length];
            for (int i = 0; i < valStrs.length; ++i) {
                vals[i] = Long.parseLong(valStrs[i].trim());
            }
            data = vals;
        } else if (this.type == ListLongDataType) {
            // remove the first and last brackets, (, and )
            String[] valStrs =
                value.substring(1, value.length() - 1).split(NUMERIC_VALUE_DELIMITER);
            List<Long> vals = new ArrayList<>(valStrs.length);
            for (int i = 0; i < valStrs.length; ++i) {
                vals.add(Long.parseLong(valStrs[i].trim()));
            }
            data = vals;
        } else if (this.type == ListStringDataType) {
            // remove the first and last brackets, (, and )
            String[] valStrs =
                value.substring(1, value.length() - 1).split(NUMERIC_VALUE_DELIMITER);
            List<String> vals = new ArrayList<>(valStrs.length);
            for (int i = 0; i < valStrs.length; ++i) {
                vals.add(valStrs[i].trim());
            }
            data = vals;
        } else if (this.type == ArrayStringDataType) {
            // remove the first and last brackets, (, and )
            data = value.substring(1, value.length() - 1).split(STRING_VALUE_DELIMITER);
        } else {
            throw new IllegalArgumentException(String.format(
                "Not support a string input for data type of %d for %s", type, value));
        }
    }

    public Object getValue() {
        return data;
    }

    public int getType() {
        return type;
    }

    public long getLongValue() {
        if (type == LongDataType) {
            return (long) data;
        } else if (type == FloatDataType
            || type == ValueUtils.DoubleDataType) {
            return ((Double)data).longValue();
        }
        throw new IllegalArgumentException("Not a numeric value : " + data);
    }

    public double getDoubleValue() {
        if (type == LongDataType) {
            return (long) data;
        } else if (type == FloatDataType
            || type == ValueUtils.DoubleDataType) {
            return (double) data;
        }
        throw new IllegalArgumentException("Not a numeric value : " + data);
    }

    @Override
    public String toString() {
        //TODO how about NULL?
        if (ValueUtils.isPrimitiveType(type)) {
            return data.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            boolean isFirst = true;
            if (type == ValueUtils.ArrayStringDataType) {
                String[] ss = (String[]) data;
                for (String s : ss) {
                    if (!isFirst) {
                        sb.append(STRING_VALUE_DELIMITER);
                    } else {
                        isFirst = false;
                    }
                    sb.append(s);
                }
            } else if (type == ArrayLongDataType) {
                long[] ll = (long[]) data;
                for (long l : ll) {
                    if (!isFirst) {
                        sb.append(NUMERIC_VALUE_DELIMITER);
                    } else {
                        isFirst = false;
                    }
                    sb.append(l);
                }
            } else {
                // TODO do we care about the difference between String and Numerics?
                List dd = (List) data;
                for (Object d : dd) {
                    if (!isFirst) {
                        sb.append(NUMERIC_VALUE_DELIMITER);
                    } else {
                        isFirst = false;
                    }
                    sb.append(d.toString());
                }
            }
            return sb.append(")").toString();
        }
    }


}
