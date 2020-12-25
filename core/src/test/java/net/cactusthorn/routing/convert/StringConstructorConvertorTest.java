package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class StringConstructorConvertorTest {

    @Test //
    public void simple() throws ConverterException {
        StringConstructorConverter c = new StringConstructorConverter();
        c.register(StringBuilder.class);
        StringBuilder s = (StringBuilder) c.convert(StringBuilder.class, "test it");
        assertEquals("test it", s.toString());
    }

    @Test //
    public void notSupported() throws ConverterException {
        StringConstructorConverter c = new StringConstructorConverter();
        assertFalse(c.register(Math.class));
    }

    @Test //
    public void nullTest() throws ConverterException {
        StringConstructorConverter c = new StringConstructorConverter();
        c.register(StringBuilder.class);
        StringBuilder s = (StringBuilder) c.convert(StringBuilder.class, (String) null);
        assertNull(s);
    }

    @Test //
    public void array() throws ConverterException {
        StringConstructorConverter c = new StringConstructorConverter();
        c.register(StringBuilder.class);
        StringBuilder[] s = (StringBuilder[]) c.convert(StringBuilder.class, new String[] { "aaa", "bbb" });
        StringBuilder[] expected = new StringBuilder[] { new StringBuilder("aaa"), new StringBuilder("bbb") };
        assertEquals(Arrays.toString(expected), Arrays.toString(s));
    }
}
