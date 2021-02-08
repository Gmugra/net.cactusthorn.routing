package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DoubleConverterTest {

    static final DoubleConverter CONVERTER = new DoubleConverter();

    @Test //
    public void test() {
        Double result = CONVERTER.convert(Double.class, null, null, "125.5");
        assertEquals(125.5d, result);
    }

    @Test //
    public void nul() throws Exception {
        Double result = CONVERTER.convert(null, null, null, null);
        assertNull(result);
    }

    @Test //
    public void empty() throws Exception {
        Double result = CONVERTER.convert(null, null, null, "  ");
        assertNull(result);
    }
}
