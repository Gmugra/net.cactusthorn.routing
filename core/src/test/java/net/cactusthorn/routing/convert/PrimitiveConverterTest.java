package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PrimitiveConverterTest {

    @Test //
    public void array() throws Exception {
        PrimitiveConverter c = new PrimitiveConverter();
        assertThrows(UnsupportedOperationException.class, () -> c.convert(Byte.TYPE, null, null, new String[0]));
    }

    @Test //
    public void byteTest() throws Exception {
        PrimitiveConverter c = new PrimitiveConverter();
        byte v = (byte) c.convert(Byte.TYPE, null, null, "1");
        assertEquals((byte) 1, v);
        v = (byte) c.convert(Byte.TYPE, null, null, (String) null);
        assertEquals((byte) 0, v);
        v = (byte) c.convert(Byte.TYPE, null, null, " ");
        assertEquals((byte) 0, v);
    }

    @Test //
    public void shortTest() throws Exception {
        PrimitiveConverter c = new PrimitiveConverter();
        short v = (short) c.convert(Short.TYPE, null, null, "1");
        assertEquals((short) 1, v);
        v = (short) c.convert(Short.TYPE, null, null, (String) null);
        assertEquals((short) 0, v);
        v = (short) c.convert(Short.TYPE, null, null, " ");
        assertEquals((byte) 0, v);
    }

    @Test //
    public void intTest() throws Exception {
        PrimitiveConverter c = new PrimitiveConverter();
        int v = (int) c.convert(Integer.TYPE, null, null, "1");
        assertEquals(1, v);
        v = (int) c.convert(Integer.TYPE, null, null, (String) null);
        assertEquals(0, v);
        v = (int) c.convert(Integer.TYPE, null, null, " ");
        assertEquals(0, v);
    }

    @Test //
    public void longTest() throws Exception {
        PrimitiveConverter c = new PrimitiveConverter();
        long v = (long) c.convert(Long.TYPE, null, null, "1");
        assertEquals(1L, v);
        v = (long) c.convert(Long.TYPE, null, null, (String) null);
        assertEquals(0L, v);
        v = (long) c.convert(Long.TYPE, null, null, " ");
        assertEquals(0L, v);
    }

    @Test //
    public void floatTest() throws Exception {
        PrimitiveConverter c = new PrimitiveConverter();
        float v = (float) c.convert(Float.TYPE, null, null, "1.1");
        assertEquals(1.1f, v);
        v = (float) c.convert(Float.TYPE, null, null, (String) null);
        assertEquals(0.0f, v);
        v = (float) c.convert(Float.TYPE, null, null, " ");
        assertEquals(0.0f, v);
    }

    @Test //
    public void doubleTest() throws Exception {
        PrimitiveConverter c = new PrimitiveConverter();
        double v = (double) c.convert(Double.TYPE, null, null, "1.1");
        assertEquals(1.1d, v);
        v = (double) c.convert(Double.TYPE, null, null, (String) null);
        assertEquals(0.0d, v);
        v = (double) c.convert(Double.TYPE, null, null, " ");
        assertEquals(0.0d, v);
    }

    @Test //
    public void charTest() throws Exception {
        PrimitiveConverter c = new PrimitiveConverter();
        char v = (char) c.convert(Character.TYPE, null, null, "abc");
        assertEquals('a', v);
        v = (char) c.convert(Character.TYPE, null, null, (String) null);
        assertEquals('\u0000', v);
        v = (char) c.convert(Character.TYPE, null, null, " ");
        assertEquals('\u0000', v);
    }

    @Test //
    public void boolTest() throws Exception {
        PrimitiveConverter c = new PrimitiveConverter();
        boolean v = (boolean) c.convert(Boolean.TYPE, null, null, "true");
        assertTrue(v);
        v = (boolean) c.convert(Boolean.TYPE, null, null, (String) null);
        assertFalse(v);
        v = (boolean) c.convert(Boolean.TYPE, null, null, " ");
        assertFalse(v);
    }

    @Test //
    public void wrongType() {
        PrimitiveConverter c = new PrimitiveConverter();
        assertThrows(IllegalArgumentException.class, () -> c.convert(java.util.Date.class, null, null, "true"));
    }
}
