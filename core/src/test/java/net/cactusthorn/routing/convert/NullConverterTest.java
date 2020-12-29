package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class NullConverterTest {

    @Test //
    public void test() throws Exception {
        NullConverter c = new NullConverter();
        Object result = c.convert(Object.class, "1");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Exception {
        NullConverter c = new NullConverter();
        Object result = c.convert(Object.class, new String[] { "125", "3456" });
        assertNull(result);
    }

    @Test //
    public void testPrimitive() throws Exception {
        NullConverter c = new NullConverter();
        byte b = (byte) c.convert(Byte.TYPE, (String) null);
        assertEquals((byte) 0, b);
    }
}
