package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class BooleanConverterTest {

    @Test //
    public void test() throws Exception {
        BooleanConverter c = new BooleanConverter();
        Boolean result = c.convert(Boolean.class, null, null, "true");
        assertTrue(result);
        result = c.convert(null, null, null, (String) null);
        assertFalse(result);
        result = c.convert(null, null, null, "  ");
        assertFalse(result);
    }

    @Test //
    public void testArray() throws Exception {
        BooleanConverter c = new BooleanConverter();
        String[] value = new String[] { "true", null, "false" };
        Boolean[] valuesAsBoolean = new Boolean[] { true, false, false };
        List<Boolean> result = c.convert(Boolean.class, null, null, value);
        assertArrayEquals(valuesAsBoolean, result.toArray());
    }

    @Test //
    public void testNullArray() throws Exception {
        BooleanConverter c = new BooleanConverter();
        List<Boolean> result = c.convert(Boolean.class, null, null, (String[]) null);
        assertTrue(result.isEmpty());
    }

    @Test //
    public void testEmptyArray() throws Exception {
        BooleanConverter c = new BooleanConverter();
        List<Boolean> result = c.convert(Boolean.class, null, null, new String[0]);
        assertTrue(result.isEmpty());
    }
}
