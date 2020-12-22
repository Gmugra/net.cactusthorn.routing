package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PrimitiveConverterTest {

    @Test //
    public void byteTest() {
        PrimitiveConverter c = new PrimitiveConverter(Byte.TYPE);
        byte v = (byte) c.convert(null, null, null, "1");
        assertEquals((byte) 1, v);
        v = (byte) c.convert(null, null, null, null);
        assertEquals((byte) 0, v);
        v = (byte) c.convert(null, null, null, " ");
        assertEquals((byte) 0, v);
    }

    @Test //
    public void shortTest() {
        PrimitiveConverter c = new PrimitiveConverter(Short.TYPE);
        short v = (short) c.convert(null, null, null, "1");
        assertEquals((short) 1, v);
        v = (short) c.convert(null, null, null, null);
        assertEquals((short) 0, v);
        v = (short) c.convert(null, null, null, " ");
        assertEquals((byte) 0, v);
    }

    @Test //
    public void intTest() {
        PrimitiveConverter c = new PrimitiveConverter(Integer.TYPE);
        int v = (int) c.convert(null, null, null, "1");
        assertEquals(1, v);
        v = (int) c.convert(null, null, null, null);
        assertEquals(0, v);
        v = (int) c.convert(null, null, null, " ");
        assertEquals(0, v);
    }

    @Test //
    public void longTest() {
        PrimitiveConverter c = new PrimitiveConverter(Long.TYPE);
        long v = (long) c.convert(null, null, null, "1");
        assertEquals(1L, v);
        v = (long) c.convert(null, null, null, null);
        assertEquals(0L, v);
        v = (long) c.convert(null, null, null, " ");
        assertEquals(0L, v);
    }

    @Test //
    public void floatTest() {
        PrimitiveConverter c = new PrimitiveConverter(Float.TYPE);
        float v = (float) c.convert(null, null, null, "1.1");
        assertEquals(1.1f, v);
        v = (float) c.convert(null, null, null, null);
        assertEquals(0.0f, v);
        v = (float) c.convert(null, null, null, " ");
        assertEquals(0.0f, v);
    }

    @Test //
    public void doubleTest() {
        PrimitiveConverter c = new PrimitiveConverter(Double.TYPE);
        double v = (double) c.convert(null, null, null, "1.1");
        assertEquals(1.1d, v);
        v = (double) c.convert(null, null, null, null);
        assertEquals(0.0d, v);
        v = (double) c.convert(null, null, null, " ");
        assertEquals(0.0d, v);
    }

    @Test //
    public void charTest() {
        PrimitiveConverter c = new PrimitiveConverter(Character.TYPE);
        char v = (char) c.convert(null, null, null, "abc");
        assertEquals('a', v);
        v = (char) c.convert(null, null, null, null);
        assertEquals('\u0000', v);
        v = (char) c.convert(null, null, null, " ");
        assertEquals('\u0000', v);
    }

    @Test //
    public void boolTest() {
        PrimitiveConverter c = new PrimitiveConverter(Boolean.TYPE);
        boolean v = (boolean) c.convert(null, null, null, "true");
        assertTrue(v);
        v = (boolean) c.convert(null, null, null, null);
        assertFalse(v);
        v = (boolean) c.convert(null, null, null, " ");
        assertFalse(v);
    }

    @Test //
    public void wrongType() {
        assertThrows(IllegalArgumentException.class, () -> new PrimitiveConverter(java.util.Date.class));
    }
}
