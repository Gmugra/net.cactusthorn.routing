package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class FloatConverterTest {

    static final FloatConverter CONVERTER = new FloatConverter();

    @Test //
    public void test() {
        Float result = CONVERTER.convert(Float.class, null, null, "125.5");
        assertEquals(125.5f, result);
    }

    @Test //
    public void nul() throws Exception {
        Float result = CONVERTER.convert(null, null, null, null);
        assertNull(result);
    }

    @Test //
    public void empty() throws Exception {
        Float result = CONVERTER.convert(null, null, null, "  ");
        assertNull(result);
    }
}
