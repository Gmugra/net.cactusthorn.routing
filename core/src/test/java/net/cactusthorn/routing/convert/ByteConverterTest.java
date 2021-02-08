package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ByteConverterTest {

    static final ByteConverter CONVERTER = new ByteConverter();

    @Test //
    public void test() {
        Byte result = CONVERTER.convert(Byte.class, null, null, "125");
        assertEquals((byte) 125, result);
    }

    @Test //
    public void nul() throws Exception {
        Byte result = CONVERTER.convert(null, null, null, null);
        assertNull(result);
    }

    @Test //
    public void empty() throws Exception {
        Byte result = CONVERTER.convert(null, null, null, "  ");
        assertNull(result);
    }
}
