package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class BooleanConverterTest {

    static final BooleanConverter CONVERTER = new BooleanConverter();

    @Test //
    public void simple() throws Exception {
        Boolean result = CONVERTER.convert(Boolean.class, null, null, "true");
        assertTrue(result);
    }

    @Test //
    public void nul() throws Exception {
        Boolean result = CONVERTER.convert(null, null, null, null);
        assertFalse(result);
    }

    @Test //
    public void empty() throws Exception {
        Boolean result = CONVERTER.convert(null, null, null, "  ");
        assertFalse(result);
    }
}
