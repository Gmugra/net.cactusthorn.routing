package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class NullConverterTest {

    @Test //
    public void test() {
        NullConverter c = new NullConverter();
        Object result = c.convert(null, null, null, "1");
        assertNull(result);
    }
}
