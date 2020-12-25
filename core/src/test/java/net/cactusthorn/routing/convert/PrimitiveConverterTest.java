package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PrimitiveConverterTest {

    @Test //
    public void byteTest() throws ConverterException {
        PrimitiveConverter c = new PrimitiveConverter();
        byte v = (byte) c.convert(Byte.TYPE, "1");
        assertEquals((byte) 1, v);
        v = (byte) c.convert(Byte.TYPE, (String) null);
        assertEquals((byte) 0, v);
        v = (byte) c.convert(Byte.TYPE, " ");
        assertEquals((byte) 0, v);
    }

    @Test //
    public void byteArrayTest() throws ConverterException {
        PrimitiveConverter c = new PrimitiveConverter();
        String[] value = new String[] { "125", "34" };
        byte[] valuesAsByte = new byte[] { (byte) 125, (byte) 34 };
        byte[] result = (byte[]) c.convert(Byte.TYPE, value);
        assertArrayEquals(valuesAsByte, result);
    }

    @Test //
    public void byteNullArrayTest() throws ConverterException {
        PrimitiveConverter c = new PrimitiveConverter();
        Object result = c.convert(Byte.TYPE, (String[]) null);
        assertNull(result);
    }

    @Test //
    public void shortTest() throws ConverterException {
        PrimitiveConverter c = new PrimitiveConverter();
        short v = (short) c.convert(Short.TYPE, "1");
        assertEquals((short) 1, v);
        v = (short) c.convert(Short.TYPE, (String) null);
        assertEquals((short) 0, v);
        v = (short) c.convert(Short.TYPE, " ");
        assertEquals((byte) 0, v);
    }

    @Test //
    public void intTest() throws ConverterException {
        PrimitiveConverter c = new PrimitiveConverter();
        int v = (int) c.convert(Integer.TYPE, "1");
        assertEquals(1, v);
        v = (int) c.convert(Integer.TYPE, (String) null);
        assertEquals(0, v);
        v = (int) c.convert(Integer.TYPE, " ");
        assertEquals(0, v);
    }

    @Test //
    public void longTest() throws ConverterException {
        PrimitiveConverter c = new PrimitiveConverter();
        long v = (long) c.convert(Long.TYPE, "1");
        assertEquals(1L, v);
        v = (long) c.convert(Long.TYPE, (String) null);
        assertEquals(0L, v);
        v = (long) c.convert(Long.TYPE, " ");
        assertEquals(0L, v);
    }

    @Test //
    public void floatTest() throws ConverterException {
        PrimitiveConverter c = new PrimitiveConverter();
        float v = (float) c.convert(Float.TYPE, "1.1");
        assertEquals(1.1f, v);
        v = (float) c.convert(Float.TYPE, (String) null);
        assertEquals(0.0f, v);
        v = (float) c.convert(Float.TYPE, " ");
        assertEquals(0.0f, v);
    }

    @Test //
    public void doubleTest() throws ConverterException {
        PrimitiveConverter c = new PrimitiveConverter();
        double v = (double) c.convert(Double.TYPE, "1.1");
        assertEquals(1.1d, v);
        v = (double) c.convert(Double.TYPE, (String) null);
        assertEquals(0.0d, v);
        v = (double) c.convert(Double.TYPE, " ");
        assertEquals(0.0d, v);
    }

    @Test //
    public void charTest() throws ConverterException {
        PrimitiveConverter c = new PrimitiveConverter();
        char v = (char) c.convert(Character.TYPE, "abc");
        assertEquals('a', v);
        v = (char) c.convert(Character.TYPE, (String) null);
        assertEquals('\u0000', v);
        v = (char) c.convert(Character.TYPE, " ");
        assertEquals('\u0000', v);
    }

    @Test //
    public void boolTest() throws ConverterException {
        PrimitiveConverter c = new PrimitiveConverter();
        boolean v = (boolean) c.convert(Boolean.TYPE, "true");
        assertTrue(v);
        v = (boolean) c.convert(Boolean.TYPE, (String) null);
        assertFalse(v);
        v = (boolean) c.convert(Boolean.TYPE, " ");
        assertFalse(v);
    }

    @Test //
    public void wrongType() {
        PrimitiveConverter c = new PrimitiveConverter();
        assertThrows(IllegalArgumentException.class, () -> c.convert(null, java.util.Date.class, "true"));
    }
}
