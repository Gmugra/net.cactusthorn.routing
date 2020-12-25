package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringConverterTest {

    @Test //
    public void test() throws ConverterException {
        StringConverter c = new StringConverter();
        String result = (String) c.convert(String.class, "abc");
        assertEquals("abc", result);
    }

    @Test //
    public void testArray() throws ConverterException {
        StringConverter c = new StringConverter();
        String[] value = new String[] { "abc", "xyz" };
        String[] result = (String[]) c.convert(String.class, value);
        assertArrayEquals(value, result);
    }

    @Test //
    public void testNullArray() throws ConverterException {
        StringConverter c = new StringConverter();
        Object result = (Object)c.convert(String.class, (String[])null);
        assertNull(result);
    }
}
