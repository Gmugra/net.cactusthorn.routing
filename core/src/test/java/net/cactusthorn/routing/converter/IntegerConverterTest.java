package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class IntegerConverterTest {

    @Test //
    public void test() {
        IntegerConverter c = new IntegerConverter();
        Integer result = c.convert(null, null, null, "125");
        assertEquals(125, result);
        result = c.convert(null, null, null, null);
        assertNull(result);
    }
}
