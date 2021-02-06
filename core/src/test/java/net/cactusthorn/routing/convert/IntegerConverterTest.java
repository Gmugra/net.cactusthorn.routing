package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class IntegerConverterTest {

    @Test //
    public void test() {
        IntegerConverter c = new IntegerConverter();
        Integer result = c.convert(Integer.class, null, null, "125");
        assertEquals(125, result);
        result = c.convert(null, null, null, (String) null);
        assertNull(result);
        result = c.convert(null, null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Throwable {
        IntegerConverter c = new IntegerConverter();
        String[] value = new String[] { "125", "  ", "3456" };
        Integer[] valuesAsInt = new Integer[] { 125, null, 3456 };
        List<Integer> result = c.convert(Integer.class, null, null, value);
        assertArrayEquals(valuesAsInt, result.toArray());
    }

    @Test //
    public void testNullArray() throws Throwable {
        IntegerConverter c = new IntegerConverter();
        List<Integer> result = c.convert(Integer.class, null, null, (String[]) null);
        assertTrue(result.isEmpty());
    }

    @Test //
    public void testEmptyArray() throws Throwable {
        IntegerConverter c = new IntegerConverter();
        List<Integer> result = c.convert(Integer.class, null, null, new String[0]);
        assertTrue(result.isEmpty());
    }
}
