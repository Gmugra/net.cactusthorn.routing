package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class StringConstructorConvertorTest {

    @Test //
    public void simple() throws Throwable {
        StringConstructorConverter c = new StringConstructorConverter();
        c.register(StringBuilder.class);
        StringBuilder s = (StringBuilder) c.convert(StringBuilder.class, null, null, "test it");
        assertEquals("test it", s.toString());
    }

    @Test //
    public void notSupported() {
        StringConstructorConverter c = new StringConstructorConverter();
        assertFalse(c.register(Math.class));
    }

    @Test //
    public void nullTest() throws Throwable {
        StringConstructorConverter c = new StringConstructorConverter();
        c.register(StringBuilder.class);
        StringBuilder s = (StringBuilder) c.convert(StringBuilder.class, null, null, (String) null);
        assertNull(s);
    }

    @Test //
    public void list() throws Throwable {
        StringConstructorConverter c = new StringConstructorConverter();
        c.register(StringBuilder.class);
        List<?> result = c.convert(StringBuilder.class, null, null, new String[] { "aaa", "bbb" });
        StringBuilder[] expected = new StringBuilder[] { new StringBuilder("aaa"), new StringBuilder("bbb") };
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i].toString(), result.get(i).toString());
        }
    }
}
