package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ShortConverterTest {

    static final ShortConverter CONVERTER = new ShortConverter();

    @Test //
    public void test() {
        Short result = CONVERTER.convert(Short.class, null, null, "125");
        assertEquals((short) 125, result);
    }

    @Test //
    public void nul() throws Exception {
        Short result = CONVERTER.convert(null, null, null, null);
        assertNull(result);
    }

    @Test //
    public void empty() throws Exception {
        Short result = CONVERTER.convert(null, null, null, "  ");
        assertNull(result);
    }
}
