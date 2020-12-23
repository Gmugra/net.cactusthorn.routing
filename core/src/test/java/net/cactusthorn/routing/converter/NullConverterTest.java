package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class NullConverterTest {

    @Test //
    public void test() throws ConverterException {
        NullConverter c = new NullConverter();
        Object result = c.convert("1");
        assertNull(result);
    }
}
