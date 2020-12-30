package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BooleanConverterTest {

    @Test //
    public void test() throws Exception {
        BooleanConverter c = new BooleanConverter();
        Boolean result = (Boolean) c.convert(Boolean.class, "true");
        assertTrue(result);
        result = (Boolean) c.convert(null, null, (String) null);
        assertFalse(result);
        result = (Boolean) c.convert(null, null, "  ");
        assertFalse(result);
    }

    @Test //
    public void testArray() throws Exception {
        BooleanConverter c = new BooleanConverter();
        String[] value = new String[] { "true", null, "false" };
        Boolean[] valuesAsBoolean = new Boolean[] { true, false, false };
        Boolean[] result = (Boolean[]) c.convert(Boolean.class, value);
        assertArrayEquals(valuesAsBoolean, result);
    }

    @Test //
    public void testNullArray() throws Exception {
        BooleanConverter c = new BooleanConverter();
        Boolean[] result = (Boolean[]) c.convert(Boolean.class, (String[]) null);
        assertNull(result);
    }
}
