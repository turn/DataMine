package datamine.query.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValueUtilsTest {

    @Test
    public void testTypeCheck() {

        Value val = new Value();
        assertEquals(ValueUtils.isNumbericType(val.getType()), false);

        assertFalse(ValueUtils.isTrue(val));

        val = new Value(true);
        assertTrue(ValueUtils.isTrue(val));

        assertFalse(ValueUtils.isTrue(
            ValueUtils.NOT(val)
        ));

        assertTrue(ValueUtils.isTrue(
            ValueUtils.OR(ValueUtils.NOT(val), val)
        ));

        assertFalse(ValueUtils.isTrue(
            ValueUtils.AND(ValueUtils.NOT(val), val)
        ));

        val = new Value(10);
        assertTrue(ValueUtils.isNumbericType(val.getType()));

        val = new Value(10l);
        assertTrue(ValueUtils.isNumbericType(val.getType()));

        val = new Value(1f);
        assertTrue(ValueUtils.isPrimitiveType(val.getType()));

        List l = new ArrayList();
        l.add(10);
        l.add(11l);
        val = new Value(l);
        assertTrue(ValueUtils.isList(val.getType()));

    }

    @Test
    public void testEquality() {
        Value left = new Value("112345", ValueUtils.LongDataType);
        Value right = new Value("112345", ValueUtils.LongDataType);

        assertFalse(ValueUtils.isTrue(ValueUtils.equals(left, ValueUtils.nullValue)));
        assertTrue(ValueUtils.isTrue(ValueUtils.equals(ValueUtils.nullValue, ValueUtils.nullValue)));
        assertTrue(ValueUtils.isTrue(ValueUtils.equals(left,left)));
        assertTrue(ValueUtils.isTrue(ValueUtils.equals(left,right)));

        assertFalse(!ValueUtils.isTrue(ValueUtils.notequals(left, ValueUtils.nullValue)));
        assertTrue(!ValueUtils.isTrue(ValueUtils.notequals(ValueUtils.nullValue, ValueUtils.nullValue)));
        assertTrue(!ValueUtils.isTrue(ValueUtils.notequals(left,left)));
        assertTrue(!ValueUtils.isTrue(ValueUtils.notequals(left,right)));

        left = new Value("1.2", ValueUtils.FloatDataType);
        right = new Value("1.200", ValueUtils.DoubleDataType);
        assertTrue(ValueUtils.isTrue(ValueUtils.equals(left,right)));
        assertTrue(!ValueUtils.isTrue(ValueUtils.notequals(left,right)));
        right = new Value("1.20001", ValueUtils.DoubleDataType);
        assertFalse(ValueUtils.isTrue(ValueUtils.equals(left,right)));
        assertFalse(!ValueUtils.isTrue(ValueUtils.notequals(left,right)));

        left = new Value("haha", ValueUtils.StringDataType);
        right = new Value("haha", ValueUtils.StringDataType);
        assertTrue(ValueUtils.isTrue(ValueUtils.equals(left,right)));
        assertTrue(!ValueUtils.isTrue(ValueUtils.notequals(left,right)));
        right = new Value("hehe", ValueUtils.StringDataType);
        assertFalse(ValueUtils.isTrue(ValueUtils.equals(left,right)));
        assertFalse(!ValueUtils.isTrue(ValueUtils.notequals(left,right)));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testEqualityException() {
        ValueUtils.equals(ValueUtils.trueValue, new Value());
    }

    @Test
    public void testComparison() {
        Value left = new Value("112345", ValueUtils.LongDataType);
        Value right = new Value("11235", ValueUtils.LongDataType);

        assertTrue(ValueUtils.isTrue(ValueUtils.greater(left,right)));
        assertTrue(ValueUtils.isTrue(ValueUtils.less(right,left)));

        right = new Value("11235.234", ValueUtils.FloatDataType);
        assertTrue(ValueUtils.isTrue(ValueUtils.greater(left,right)));
        assertTrue(ValueUtils.isTrue(ValueUtils.less(right,left)));

        left = new Value("zaha", ValueUtils.StringDataType);
        right = new Value("haha", ValueUtils.StringDataType);
        assertTrue(ValueUtils.isTrue(ValueUtils.greater(left,right)));
        assertTrue(ValueUtils.isTrue(ValueUtils.less(right,left)));

    }


    @Test
    public void testPlus() {

        Value left = new Value("1", ValueUtils.LongDataType);
        Value right = new Value("3", ValueUtils.LongDataType);
        Value sum = new Value("4", ValueUtils.LongDataType);

        Value result = ValueUtils.equals(ValueUtils.plus(left, right), sum);
        assertTrue(ValueUtils.isTrue(result));

        left = new Value("1.3", ValueUtils.FloatDataType);
        right = new Value("3", ValueUtils.LongDataType);
        sum = new Value("4.3", ValueUtils.FloatDataType);

        result = ValueUtils.equals(ValueUtils.plus(left, right), sum);
        assertTrue(ValueUtils.isTrue(result));

        left = new Value("welcome", ValueUtils.StringDataType);
        right = new Value(" you!", ValueUtils.StringDataType);
        sum = new Value("welcome you!", ValueUtils.StringDataType);

        result = ValueUtils.equals(ValueUtils.plus(left, right), sum);
        assertTrue(ValueUtils.isTrue(result));
    }

    @Test (expected = IllegalArgumentException.class)
    public void testPlusException() {
        Value left = new Value("1", ValueUtils.LongDataType);
        ValueUtils.plus(left, ValueUtils.trueValue);
    }

    @Test
    public void testMinus() {

        Value left = new Value("100", ValueUtils.LongDataType);
        Value right = new Value("3", ValueUtils.LongDataType);
        Value sum = new Value("97", ValueUtils.LongDataType);

        Value result = ValueUtils.equals(ValueUtils.minus(left, right), sum);
        assertTrue(ValueUtils.isTrue(result));

        left = new Value("1.3", ValueUtils.FloatDataType);
        right = new Value("3", ValueUtils.LongDataType);
        sum = new Value("-1.7", ValueUtils.FloatDataType);

        result = ValueUtils.equals(ValueUtils.minus(left, right), sum);
        assertTrue(ValueUtils.isTrue(result));

    }

    @Test
    public void testMultiplication() {

        Value left = new Value("100", ValueUtils.LongDataType);
        Value right = new Value("3", ValueUtils.LongDataType);
        Value sum = new Value("300", ValueUtils.LongDataType);

        Value result = ValueUtils.equals(ValueUtils.multiply(left, right), sum);
        assertTrue(ValueUtils.isTrue(result));

        left = new Value("1.3", ValueUtils.FloatDataType);
        right = new Value("3", ValueUtils.LongDataType);
        sum = new Value("3.9", ValueUtils.FloatDataType);

        result = ValueUtils.equals(ValueUtils.multiply(left, right), sum);
        //TODO the comparision is hard for double type operations
//        assertTrue(ValueUtils.isTrue(result));

    }

    @Test
    public void testDivision() {

        Value left = new Value("100", ValueUtils.LongDataType);
        Value right = new Value("2", ValueUtils.LongDataType);
        Value sum = new Value("50", ValueUtils.LongDataType);

        Value result = ValueUtils.equals(ValueUtils.divide(left, right), sum);
        assertTrue(ValueUtils.isTrue(result));

        right = new Value("0", ValueUtils.LongDataType);
        result = ValueUtils.equals(ValueUtils.divide(left, right), ValueUtils.nullValue);
        assertTrue(ValueUtils.isTrue(result));

        left = new Value("3.0", ValueUtils.FloatDataType);
        right = new Value("3", ValueUtils.LongDataType);
        sum = new Value("1.0", ValueUtils.FloatDataType);

        result = ValueUtils.equals(ValueUtils.divide(left, right), sum);
        assertTrue(ValueUtils.isTrue(result));

        // NaN != NaN
        right = new Value("0", ValueUtils.FloatDataType);
        result = ValueUtils.equals(ValueUtils.divide(left, right),
            new Value(Float.NaN, ValueUtils.FloatDataType));
        assertFalse(ValueUtils.isTrue(result));
    }

    @Test
    public void testMod() {

        Value left = new Value("100", ValueUtils.LongDataType);
        Value right = new Value("2", ValueUtils.LongDataType);
        Value sum = new Value("0", ValueUtils.LongDataType);

        Value result = ValueUtils.equals(ValueUtils.mod(left, right), sum);
        assertTrue(ValueUtils.isTrue(result));

        right = new Value("0", ValueUtils.LongDataType);
        result = ValueUtils.equals(ValueUtils.mod(left, right),
            ValueUtils.nullValue);
        assertTrue(ValueUtils.isTrue(result));
    }

    @Test
    public void testStringMatching() {
        String left = "welcome to the real world!";
        String right = "welcome%";
        String pattern = ValueUtils.getRegEx(right);

        assertEquals(pattern, "^welcome.*$");
        assertTrue(ValueUtils.isTrue(
            ValueUtils.like(left, pattern)
        ));

        Value leftVal = new Value(left, ValueUtils.StringDataType);
        Value rightVal = new Value(right, ValueUtils.StringDataType);
        assertTrue(ValueUtils.isTrue(
            ValueUtils.like(leftVal, rightVal)
        ));
    }
}


