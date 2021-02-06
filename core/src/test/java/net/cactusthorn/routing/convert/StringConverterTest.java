package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class StringConverterTest {

    @Test //
    public void test() {
        StringConverter c = new StringConverter();
        String result = c.convert(String.class, null, null, "abc");
        assertEquals("abc", result);
    }

    @Test //
    public void testArray() throws Throwable {
        StringConverter c = new StringConverter();
        String[] value = new String[] { "abc", "xyz" };
        List<String> result = c.convert(String.class, null, null, value);
        assertArrayEquals(value, result.toArray());
    }

    @Test //
    public void testNullArray() throws Throwable {
        StringConverter c = new StringConverter();
        List<String> result = c.convert(String.class, null, null, (String[]) null);
        assertTrue(result.isEmpty());
    }

    @Test //
    public void testEmptyArray() throws Throwable {
        StringConverter c = new StringConverter();
        List<String> result = c.convert(String.class, null, null, new String[0]);
        assertTrue(result.isEmpty());
    }
}
