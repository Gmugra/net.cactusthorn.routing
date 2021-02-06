package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class LongConverterTest {

    @Test //
    public void test() {
        LongConverter c = new LongConverter();
        Long result = c.convert(Long.class, null, null, "125");
        assertEquals(125L, result);
        result = c.convert(null, null, null, (String) null);
        assertNull(result);
        result = c.convert(null, null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Throwable {
        LongConverter c = new LongConverter();
        String[] value = new String[] { "125", "  ", "16" };
        Long[] valuesAsLong = new Long[] { 125L, null, 16L };
        List<Long> result = c.convert(Long.class, null, null, value);
        assertArrayEquals(valuesAsLong, result.toArray());
    }

    @Test //
    public void testNullArray() throws Throwable {
        LongConverter c = new LongConverter();
        List<Long> result = c.convert(Long.class, null, null, (String[]) null);
        assertTrue(result.isEmpty());
    }

    @Test //
    public void testEmptyArray() throws Throwable {
        LongConverter c = new LongConverter();
        List<Long> result = c.convert(Long.class, null, null, new String[0]);
        assertTrue(result.isEmpty());
    }
}
