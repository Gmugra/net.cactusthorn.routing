package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class LongConverterTest {

    static final LongConverter CONVERTER = new LongConverter();

    @Test //
    public void test() {
        Long result = CONVERTER.convert(Long.class, null, null, "125");
        assertEquals(125L, result);
    }

    @Test //
    public void nul() throws Exception {
        Long result = CONVERTER.convert(null, null, null, null);
        assertNull(result);
    }

    @Test //
    public void empty() throws Exception {
        Long result = CONVERTER.convert(null, null, null, "  ");
        assertNull(result);
    }
}
