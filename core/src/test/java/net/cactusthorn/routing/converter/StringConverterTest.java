package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StringConverterTest {

    @Test //
    public void test() throws ConverterException {
        StringConverter c = new StringConverter();
        String result = c.convert("abc");
        assertEquals("abc", result);
    }
}
