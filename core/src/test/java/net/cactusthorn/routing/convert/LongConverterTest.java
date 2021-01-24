package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class LongConverterTest {

    @Test //
    public void test() throws Exception {
        LongConverter c = new LongConverter();
        Long result = (Long) c.convert(Long.class, null, null, "125");
        assertEquals(125L, result);
        result = (Long) c.convert(null, null, null, (String) null);
        assertNull(result);
        result = (Long) c.convert(null, null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Exception {
        LongConverter c = new LongConverter();
        String[] value = new String[] { "125", "  ", "16" };
        Long[] valuesAsLong = new Long[] { 125L, null, 16L };
        Long[] result = (Long[]) c.convert(Long.class, null, null, value);
        assertArrayEquals(valuesAsLong, result);
    }

    @Test //
    public void testNullArray() throws Exception {
        LongConverter c = new LongConverter();
        Object result = (Object) c.convert(Long.class, null, null, (String[]) null);
        assertNull(result);
    }
}
