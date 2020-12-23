package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PrimitiveConverterTest {

    @Test //
    public void byteTest() {
        PrimitiveConverter c = new PrimitiveConverter();
        byte v = (byte) c.convert(null, Byte.TYPE, "1");
        assertEquals((byte) 1, v);
        v = (byte) c.convert(null, Byte.TYPE, null);
        assertEquals((byte) 0, v);
        v = (byte) c.convert(null, Byte.TYPE, " ");
        assertEquals((byte) 0, v);
    }

    @Test //
    public void shortTest() {
        PrimitiveConverter c = new PrimitiveConverter();
        short v = (short) c.convert(null, Short.TYPE, "1");
        assertEquals((short) 1, v);
        v = (short) c.convert(null, Short.TYPE, null);
        assertEquals((short) 0, v);
        v = (short) c.convert(null, Short.TYPE, " ");
        assertEquals((byte) 0, v);
    }

    @Test //
    public void intTest() {
        PrimitiveConverter c = new PrimitiveConverter();
        int v = (int) c.convert(null, Integer.TYPE, "1");
        assertEquals(1, v);
        v = (int) c.convert(null, Integer.TYPE, null);
        assertEquals(0, v);
        v = (int) c.convert(null, Integer.TYPE, " ");
        assertEquals(0, v);
    }

    @Test //
    public void longTest() {
        PrimitiveConverter c = new PrimitiveConverter();
        long v = (long) c.convert(null, Long.TYPE, "1");
        assertEquals(1L, v);
        v = (long) c.convert(null, Long.TYPE, null);
        assertEquals(0L, v);
        v = (long) c.convert(null, Long.TYPE, " ");
        assertEquals(0L, v);
    }

    @Test //
    public void floatTest() {
        PrimitiveConverter c = new PrimitiveConverter();
        float v = (float) c.convert(null, Float.TYPE, "1.1");
        assertEquals(1.1f, v);
        v = (float) c.convert(null, Float.TYPE, null);
        assertEquals(0.0f, v);
        v = (float) c.convert(null, Float.TYPE, " ");
        assertEquals(0.0f, v);
    }

    @Test //
    public void doubleTest() {
        PrimitiveConverter c = new PrimitiveConverter();
        double v = (double) c.convert(null, Double.TYPE, "1.1");
        assertEquals(1.1d, v);
        v = (double) c.convert(null, Double.TYPE, null);
        assertEquals(0.0d, v);
        v = (double) c.convert(null, Double.TYPE, " ");
        assertEquals(0.0d, v);
    }

    @Test //
    public void charTest() {
        PrimitiveConverter c = new PrimitiveConverter();
        char v = (char) c.convert(null, Character.TYPE, "abc");
        assertEquals('a', v);
        v = (char) c.convert(null, Character.TYPE, null);
        assertEquals('\u0000', v);
        v = (char) c.convert(null, Character.TYPE, " ");
        assertEquals('\u0000', v);
    }

    @Test //
    public void boolTest() {
        PrimitiveConverter c = new PrimitiveConverter();
        boolean v = (boolean) c.convert(null, Boolean.TYPE, "true");
        assertTrue(v);
        v = (boolean) c.convert(null, Boolean.TYPE, null);
        assertFalse(v);
        v = (boolean) c.convert(null, Boolean.TYPE, " ");
        assertFalse(v);
    }

    @Test //
    public void wrongType() {
        PrimitiveConverter c = new PrimitiveConverter();
        assertThrows(IllegalArgumentException.class, () -> c.convert(null, java.util.Date.class, "true"));
    }
}
