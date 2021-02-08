package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class StringConverterTest {

    @Test //
    public void test() {
        StringConverter c = new StringConverter();
        String result = c.convert(String.class, null, null, "abc");
        assertEquals("abc", result);
    }
}
