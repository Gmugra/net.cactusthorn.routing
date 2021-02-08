package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class IntegerConverterTest {

    static final IntegerConverter CONVERTER = new IntegerConverter();

    @Test //
    public void test() {
        Integer result = CONVERTER.convert(Integer.class, null, null, "125");
        assertEquals(125, result);
    }

    @Test //
    public void nul() throws Exception {
        Integer result = CONVERTER.convert(null, null, null, null);
        assertNull(result);
    }

    @Test //
    public void empty() throws Exception {
        Integer result = CONVERTER.convert(null, null, null, "  ");
        assertNull(result);
    }
}
