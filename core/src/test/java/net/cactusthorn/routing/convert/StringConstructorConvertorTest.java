package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

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
}
