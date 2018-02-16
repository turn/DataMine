package datamine.query.data;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ValueTest {

    @Test
    public void testConstrutors() {

        Value val = new Value();
        assertEquals(val.getType(), ValueUtils.UnknownType);

        val = new Value(10);
        assertEquals(val.getType(), ValueUtils.LongDataType);

        val = new Value(10l);
        assertEquals(val.getType(), ValueUtils.LongDataType);

        val = new Value((short)10);
        assertEquals(val.getType(), ValueUtils.LongDataType);

        val = new Value((byte)10);
        assertEquals(val.getType(), ValueUtils.LongDataType);

        val = new Value(1f);
        assertEquals(val.getType(), ValueUtils.DoubleDataType);

        val = new Value(1d);
        assertEquals(val.getType(), ValueUtils.DoubleDataType);

        val = new Value("haha");
        assertEquals(val.getType(), ValueUtils.StringDataType);

        val = new Value(true);
        assertEquals(val.getType(), ValueUtils.BooleanDataType);

        val = new Value(new long[]{0, 1l, (byte)2, (short)3});
        assertEquals(val.getType(), ValueUtils.ArrayLongDataType);

        val = new Value(new String[]{"haha", "hehe"});
        assertEquals(val.getType(), ValueUtils.ArrayStringDataType);

        List l = new ArrayList();
        l.add(10);
        l.add(11l);
        val = new Value(l);
        assertEquals(val.getType(), ValueUtils.ListLongDataType);
        assertEquals(val.toString(), "(10,11)");

        l = new ArrayList();
        l.add(1.0);
        l.add(11l);
        val = new Value(l);
        assertEquals(val.getType(), ValueUtils.ListFloatDataType);

        l = new ArrayList();
        l.add("asd");
        l.add(11l);
        val = new Value(l);
        assertEquals(val.getType(), ValueUtils.ListStringDataType);
    }

    @Test
    public void testConstructor1() {
        Value val = new Value("1", ValueUtils.LongDataType);
        assertEquals(  1, val.getLongValue());
        assertEquals(val.toString(), "1");

        val = new Value("1.1", ValueUtils.DoubleDataType);
        assertEquals(1.1, val.getDoubleValue(), 0.0000001);
        assertEquals(val.toString(), "1.1");

        val = new Value("true", ValueUtils.BooleanDataType);
        assertTrue((Boolean) val.getValue());
        assertEquals(val.toString(), "true");

        val = new Value("true", ValueUtils.StringDataType);
        assertEquals("true", val.toString());
        assertEquals(val.toString(), "true");

        val = new Value("true", ValueUtils.NullDataType);
        assertEquals("true", val.toString());
        assertEquals(val.toString(), "true");

        val = new Value("(1, 2, 3, 4)", ValueUtils.ArrayLongDataType);
        assertArrayEquals(new long[]{1,2,3,4}, (long[]) val.getValue());
        assertEquals(val.toString(), "(1,2,3,4)");

        val = new Value("(1``2``3``4)", ValueUtils.ArrayStringDataType);
        assertArrayEquals(new String[]{"1","2","3","4"}, (String[]) val.getValue());
        assertEquals(val.toString(), "(1``2``3``4)");
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructionException() {
        List l = null;
        new Value(l);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructionException2() {
        List l = new ArrayList();
        l.add(true);
        l.add(11l);
        new Value(l);
    }

}


