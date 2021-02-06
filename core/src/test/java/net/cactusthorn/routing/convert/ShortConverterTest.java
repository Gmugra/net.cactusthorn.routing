package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ShortConverterTest {

    @Test //
    public void test() {
        ShortConverter c = new ShortConverter();
        Short result = c.convert(Short.class, null, null, "125");
        assertEquals((short) 125, result);
        result = c.convert(null, null, null, (String) null);
        assertNull(result);
        result = c.convert(null, null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Throwable {
        ShortConverter c = new ShortConverter();
        String[] value = new String[] { "125", "  ", "3456" };
        Short[] valuesAsShort = new Short[] { 125, null, 3456 };
        List<Short> result = c.convert(Short.class, null, null, value);
        assertArrayEquals(valuesAsShort, result.toArray());
    }

    @Test //
    public void testNullArray() throws Throwable {
        ShortConverter c = new ShortConverter();
        List<Short> result = c.convert(Short.class, null, null, (String[]) null);
        assertTrue(result.isEmpty());
    }

    @Test //
    public void testEmptyArray() throws Throwable {
        ShortConverter c = new ShortConverter();
        List<Short> result = c.convert(Short.class, null, null, new String[0]);
        assertTrue(result.isEmpty());
    }
}
