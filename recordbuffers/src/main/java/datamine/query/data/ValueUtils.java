package datamine.query.data;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import datamine.storage.idl.type.PrimitiveType;

/**
 * It is a collection of commonly used routines that all other processes need.
 */
public class ValueUtils implements Serializable {

    /**
     * Constants as IDs for data type
     */
    public static final int UnknownType = PrimitiveType.UNKNOWN.getId();
    public static final int NullDataType = PrimitiveType.NULL.getId();
    public static final int LongDataType = PrimitiveType.INT64.getId();
    public static final int FloatDataType = PrimitiveType.FLOAT.getId();
    public static final int DoubleDataType = PrimitiveType.DOUBLE.getId();
    public static final int StringDataType = PrimitiveType.STRING.getId();
    public static final int BooleanDataType = PrimitiveType.BOOL.getId();
    public static final int ListLongDataType = 100;
    public static final int ListFloatDataType = 101;
    public static final int ListStringDataType = 102;
    public static final int ArrayLongDataType = 103;
    public static final int ArrayStringDataType = 104;

    /**
     * Constant Values
     */
    public static Value trueValue   = new Value(true, ValueUtils.BooleanDataType);
    public static Value falseValue  = new Value(false, ValueUtils.BooleanDataType);
    public static Value nullValue   = new Value("null", ValueUtils.NullDataType);

    /**
     * Dummy Constructor to prevent from creating instance
     */
    private ValueUtils() {
    }


    /**
     * Check if the input type is of numeric
     * @param type the type ID
     * @return true if the type ID is of numeric value
     */
    public static boolean isNumbericType(int type) {
        return type == FloatDataType || type == LongDataType || type == DoubleDataType;
    }

    /**
     * Check if the input type is primitive
     * @param type the type ID
     * @return true if the type ID is of primitive value, false otherwise.
     */
    public static boolean isPrimitiveType(int type) {
        return !isList(type) &&
            type != ArrayLongDataType &&
            type != ArrayStringDataType;
    }

    /**
     * Check if the input type is of LIST
     * @param type the type ID
     * @return true if the type ID is of LIST, false otherwise.
     */
    public static boolean isList(int type) {
        return type == ListLongDataType || type == ListFloatDataType || type == ListStringDataType;
    }


    private static Value compareNullValues(Value left, Value right) {

        if (left == null && right.getType() == NullDataType
            || right == null && right.getType() == NullDataType
            || left == null && right == null
            || left == right) {
            return trueValue;
        }

        if (left == null || right == null
            || left.getType() == NullDataType || right.getType() == NullDataType) {
            return falseValue;
        }

        return nullValue;
    }

    /**
     * Check if the input two values are equal
     * @param left one input value
     * @param right the other input value
     * @return true if the input are the same in terms of their values
     */
    public static Value equals(Value left, Value right) {

        Value ret = compareNullValues(left, right);

        if (ret != nullValue) {
            return ret;
        }

        if (left.getType() == LongDataType
            && right.getType() == LongDataType) {
            Number l = (Number) left.getValue();
            Number r = (Number) right.getValue();
            return l.longValue() == r.longValue() ? trueValue : falseValue;
        }

        if (isNumbericType(left.getType()) && isNumbericType(right.getType())) {
            Number l = (Number) left.getValue();
            Number r = (Number) right.getValue();
            return l.doubleValue() == r.doubleValue() ? trueValue : falseValue;
        }

        if (left.getType() == right.getType()) {
            return Objects.equals(left.getValue(), right.getValue())
                ? trueValue
                : falseValue;
        }

        throw new IllegalArgumentException(
            String.format("Incompatible data types: %s v.s. %s",
            left.getType(), right.getType()));
    }

    /**
     * Check if the input two values are unequal
     * @param left one input value
     * @param right the other input value
     * @return true if the input are not the same in terms of their values
     */
    public static Value notequals(Value left, Value right) {
        Value isEqual = equals(left, right);
        return isEqual == trueValue ? falseValue : trueValue;
    }

    /**
     * Compare two numeric or string values to see if the first is larger than the other.
     * @param left the one value
     * @param right the other value
     * @return true if the left value is larger than the right.
     */
    public static Value greater(Value left, Value right) {

        if (left == null || right == null ||
            left.getType() == NullDataType ||
            right.getType() == NullDataType ||
            left.getValue() == null ||
            right.getValue() == null) {
            return nullValue;
        }

        if (left.getType() == LongDataType &&
            right.getType() == LongDataType) {
            Number d1 = (Number) left.getValue();
            Number d2 = (Number) right.getValue();
            return d1.longValue() > d2.longValue() ? trueValue : falseValue;
        }

        if (isNumbericType(left.getType()) &&
            isNumbericType(right.getType())) {
            Number d1 = (Number) left.getValue();
            Number d2 = (Number) right.getValue();
            return d1.doubleValue() > d2.doubleValue() ? trueValue : falseValue;
        }

        if (left.getType() == StringDataType &&
            right.getType() == StringDataType) {
            String l = (String) left.getValue();
            String r = (String) right.getValue();
            return l.compareTo(r) > 0 ? trueValue : falseValue;
        }

        throw new IllegalArgumentException(String.format(
            "Incompatible data types: %s v.s. %s",
            left.getType(), right.getType()));
    }

    /**
     * Compare two numeric or string values to see if the first is less than the other.
     * @param left the one value
     * @param right the other value
     * @return true if the left value is less than the right.
     */
    public static Value less(Value left, Value right) {

        if (left == null || right == null ||
            left.getType() == NullDataType ||
            right.getType() == NullDataType ||
            left.getValue() == null ||
            right.getValue() == null) {
            return nullValue;
        }

        if (left.getType() == LongDataType &&
            right.getType() == LongDataType) {
            Number d1 = (Number) left.getValue();
            Number d2 = (Number) right.getValue();
            return d1.longValue() < d2.longValue() ? trueValue : falseValue;
        }

        if (isNumbericType(left.getType()) &&
            isNumbericType(right.getType())) {
            Number d1 = (Number) left.getValue();
            Number d2 = (Number) right.getValue();
            return d1.doubleValue() < d2.doubleValue() ? trueValue : falseValue;
        }

        if (left.getType() == StringDataType &&
            right.getType() == StringDataType) {
            String l = (String) left.getValue();
            String r = (String) right.getValue();
            return l.compareTo(r) < 0 ? trueValue : falseValue;
        }

        throw new IllegalArgumentException(String.format(
            "Incompatible data types: %s v.s. %s",
            left.getType(), right.getType()));
    }


    private static Value numbericOpr(Value left, Value right, String opr) {

        if (left == null || right == null ||
            left.getType() == NullDataType ||
            right.getType() == NullDataType ||
            left.getValue() == null ||
            right.getValue() == null) {
            return nullValue;
        }

        if (left.getType() == LongDataType
            && right.getType() == LongDataType) {
            Number l = (Number) left.getValue();
            Number r = (Number) right.getValue();
            switch (opr) {
                case "+":
                    return new Value(l.longValue() + r.longValue(),
                            LongDataType);
                case "-":
                    return new Value(l.longValue() - r.longValue(),
                            LongDataType);
                case "*":
                    return new Value(l.longValue() * r.longValue(),
                            LongDataType);
                case "/":
                    if (r.longValue() == 0l) {
                        return nullValue;
                    } else {
                        return new Value(l.longValue() / r.longValue(),
                            LongDataType);
                    }
                case "%":
                    if (r.longValue() == 0l) {
                        return nullValue;
                    } else {
                        return new Value(l.longValue() % r.longValue(),
                            LongDataType);
                    }
            }
        }

        if (isNumbericType(left.getType())
            && isNumbericType(right.getType())) {
            Number l = (Number) left.getValue();
            Number r = (Number) right.getValue();
            switch (opr) {
                case "+":
                    return new Value(l.doubleValue() + r.doubleValue(),
                        FloatDataType);
                case "-":
                    return new Value(l.doubleValue() - r.doubleValue(),
                        FloatDataType);
                case "*":
                    return new Value(l.doubleValue() * r.doubleValue(),
                        FloatDataType);
                case "/":
                    if (r.floatValue() == 0l) {
                        return new Value(Float.NaN, FloatDataType);
                    } else {
                        return new Value(l.doubleValue() / r.doubleValue(),
                            FloatDataType);
                    }
            }
        }

        if (left.getType() == StringDataType ||
            right.getType() == StringDataType) {
            switch (opr) {
                case "+":
                    return new Value(left.getValue().toString() +
                        right.getValue().toString(), StringDataType);
            }
        }

        throw new IllegalArgumentException(
            "Incompatible data types: " + left.getType() + " v.s. " + right.getType());
    }

    /**
     * Get the sum of the input values
     * @param left the one input
     * @param right the other input
     * @return the sum of the input values
     */
    public static Value plus(Value left, Value right) {
        return numbericOpr(left, right, "+");
    }

    /**
     * Apply subtraction to the input values
     * @param left the one input
     * @param right the other input
     * @return the result of subtracting the right from the left number
     */
    public static Value minus(Value left, Value right) {
        return numbericOpr(left, right, "-");
    }

    /**
     * Get multiplication of the input values
     * @param left the one input
     * @param right the other input
     * @return the multiplication of the input values
     */
    public static Value multiply(Value left, Value right) {
        return numbericOpr(left, right, "*");
    }

    /**
     * Apply division to the input values
     * @param left the one input
     * @param right the other input
     * @return the result of dividing the left by the right
     */
    public static Value divide(Value left, Value right) {
        return numbericOpr(left, right, "/");
    }

    /**
     * Get the remainder of the input values
     * @param left the one input
     * @param right the other input
     * @return the remainder of dividing the left by the right
     */
    public static Value mod(Value left, Value right) {
        return numbericOpr(left, right, "%");
    }

    /**
     * Translate a wildcard string into regular expression
     * @param psedoRegEx the input wildcard string
     * @return the regular expression of the input wildcard string
     */
    public static String getRegEx(String psedoRegEx) {
        StringBuilder s = new StringBuilder();
        s.append('^');
        for (int i = 0, is = psedoRegEx.length(); i < is; i++) {
            char c = psedoRegEx.charAt(i);
            switch (c) {
                case '%':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                // escape special regexp-characters
                case '{':
                case '}':
                case '[':
                case ']':
                case '(':
                case ')':
                case '.':
                case '|':
                case '$':
                case '^':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return (s.toString());
    }

    /**
     * Check if the input String matches the right pattern
     * @param txt the input string
     * @param pattern the pattern
     * @return true if the input string matches the pattern
     */
    public static Value like(Value txt, Value pattern) {

        if (txt == null || pattern == null ||
            txt.getType() == NullDataType ||
            pattern.getType() == NullDataType ||
            txt.getValue() == null ||
            pattern.getValue() == null) {
            return nullValue;
        }

        if (txt.getType() == StringDataType &&
            pattern.getType() == StringDataType) {
            String l = (String) txt.getValue();
            String r = (String) pattern.getValue();
            return Pattern.matches(getRegEx(r), l)
                ? trueValue
                : falseValue;
        }

        throw new IllegalArgumentException(String.format(
            "Incompatible data types: %s v.s. %s",
            txt.getType(), pattern.getType()));
    }

    /**
     * Check if the input String matches the right pattern
     * @param txt the input string
     * @param pattern the pattern
     * @return true if the input string matches the pattern
     */
    public static Value like(String txt, String pattern) {
        if (txt == null || pattern == null) {
            return nullValue;
        }
        return Pattern.matches(pattern, txt) ? trueValue : falseValue;
    }


    /**
     * Get the sum of the input values
     * @param left the one input
     * @param right the other input
     * @return the sum of the input values
     */
    public static Value AND(Value left, Value right) {

        if (left == null || right == null ||
            left.getType() == NullDataType ||
            right.getType() == NullDataType ||
            left.getValue() == null ||
            right.getValue() == null) {
            return nullValue;
        }

        if (left.getType() == BooleanDataType &&
            right.getType() == BooleanDataType) {
            Boolean l = (Boolean) left.getValue();
            Boolean r = (Boolean) right.getValue();
            return l.booleanValue() && r.booleanValue()
                ? trueValue
                : falseValue;
        }

        throw new IllegalArgumentException(
            "Incompatible data types: " + left.getType() + " v.s. " + right.getType());
    }

    /**
     * Get the sum of the input values
     * @param left the one input
     * @param right the other input
     * @return the sum of the input values
     */
    public static Value OR(Value left, Value right) {

        if (left == null || right == null ||
            left.getType() == NullDataType ||
            right.getType() == NullDataType ||
            left.getValue() == null ||
            right.getValue() == null) {
            return nullValue;
        }

        if (left.getType() == BooleanDataType &&
            right.getType() == BooleanDataType) {
            Boolean l = (Boolean) left.getValue();
            Boolean r = (Boolean) right.getValue();
            return l.booleanValue() || r.booleanValue()
                ? trueValue
                : falseValue;
        }

        throw new IllegalArgumentException(String.format(
            "Incompatible data types: %s v.s. %s",
            left.getType(), right.getType()));
    }

    /**
     * Return false if the input value is a TRUE value, or true if a FALSE value.
     * @param value the input Boolean value
     * @return false if the input value is a TRUE value, or true if a FALSE value
     */
    public static Value NOT(Value value) {

        if (value == null || value.getType() == NullDataType ||
            value.getValue() == null) {
            return nullValue;
        }

        if (value.getType() == BooleanDataType) {
            Boolean l = (Boolean) value.getValue();
            return l.booleanValue() ? falseValue : trueValue;
        }

        throw new IllegalArgumentException(
            "Incompatible data types: " + value.getType());
    }

    /**
     * Check if the input value is a TRUE value
     * @param value the input boolean value
     * @return true if the input value is TRUE value, false otherwise.
     */
    public static boolean isTrue(Value value) {

        if (value == null || value.getType() == NullDataType ||
            value.getValue() == null) {
            return false;
        }

        Boolean l = (Boolean) value.getValue();
        return l.booleanValue();
    }

}
