package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

public class StringConstructorConvertorTest {

    @Test //
    public void simple() throws Exception {
        StringConstructorConverter c = new StringConstructorConverter();
        c.register(StringBuilder.class);
        StringBuilder s = (StringBuilder) c.convert(StringBuilder.class, null, null, "test it");
        assertEquals("test it", s.toString());
    }

    @Test //
    public void notSupported() throws Exception {
        StringConstructorConverter c = new StringConstructorConverter();
        assertFalse(c.register(Math.class));
    }

    @Test //
    public void nullTest() throws Exception {
        StringConstructorConverter c = new StringConstructorConverter();
        c.register(StringBuilder.class);
        StringBuilder s = (StringBuilder) c.convert(StringBuilder.class, null, null, (String) null);
        assertNull(s);
    }

    @Test //
    public void array() throws Exception {
        StringConstructorConverter c = new StringConstructorConverter();
        c.register(StringBuilder.class);
        StringBuilder[] s = (StringBuilder[]) c.convert(StringBuilder.class, null, null, new String[] { "aaa", "bbb" });
        StringBuilder[] expected = new StringBuilder[] { new StringBuilder("aaa"), new StringBuilder("bbb") };
        assertEquals(Arrays.toString(expected), Arrays.toString(s));
    }
}
