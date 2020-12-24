package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class NullConverterTest {

    @Test //
    public void test() throws ConverterException {
        NullConverter c = new NullConverter();
        Object result = c.convert(Object.class, "1");
        assertNull(result);
    }

    @Test //
    public void testArray() throws ConverterException {
        NullConverter c = new NullConverter();
        Object result = c.convert(Object.class, new String[] { "125", "3456" });
        assertNull(result);
    }
}
