package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class IntegerConverterTest {

    @Test //
    public void test() throws Exception {
        IntegerConverter c = new IntegerConverter();
        Integer result = (Integer) c.convert(Integer.class, "125");
        assertEquals(125, result);
        result = (Integer) c.convert(null, (String) null);
        assertNull(result);
        result = (Integer) c.convert(null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Exception {
        IntegerConverter c = new IntegerConverter();
        String[] value = new String[] { "125", "  ", "3456" };
        Integer[] valuesAsInt = new Integer[] { 125, null, 3456 };
        Integer[] result = (Integer[]) c.convert(Integer.class, value);
        assertArrayEquals(valuesAsInt, result);
    }

    @Test //
    public void testNullArray() throws Exception {
        IntegerConverter c = new IntegerConverter();
        Object result = (Object) c.convert(Integer.class, (String[]) null);
        assertNull(result);
    }
}
