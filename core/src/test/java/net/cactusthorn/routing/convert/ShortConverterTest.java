package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ShortConverterTest {

    @Test //
    public void test() throws Exception {
        ShortConverter c = new ShortConverter();
        Short result = (Short) c.convert(Short.class, null, null, "125");
        assertEquals((short)125, result);
        result = (Short) c.convert(null, null, null, (String) null);
        assertNull(result);
        result = (Short) c.convert(null, null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Exception {
        ShortConverter c = new ShortConverter();
        String[] value = new String[] { "125", "  ", "3456" };
        Short[] valuesAsShort = new Short[] { 125, null, 3456 };
        Short[] result = (Short[]) c.convert(Short.class, null, null, value);
        assertArrayEquals(valuesAsShort, result);
    }

    @Test //
    public void testNullArray() throws Exception {
        ShortConverter c = new ShortConverter();
        Object result = (Object) c.convert(Short.class, null, null, (String[]) null);
        assertNull(result);
    }
}
