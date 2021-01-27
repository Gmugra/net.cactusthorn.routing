package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class ByteConverterTest {

    @Test //
    public void test() throws Exception {
        ByteConverter c = new ByteConverter();
        Byte result = c.convert(Byte.class, null, null, "125");
        assertEquals((byte) 125, result);
        result = c.convert(null, null, null, (String) null);
        assertNull(result);
        result = c.convert(null, null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Exception {
        ByteConverter c = new ByteConverter();
        String[] value = new String[] { "125", "  ", "16" };
        Byte[] valuesAsByte = new Byte[] { 125, null, 16 };
        List<Byte> result = c.convert(Byte.class, null, null, value);
        assertArrayEquals(valuesAsByte, result.toArray());
    }

    @Test //
    public void testNullArray() throws Exception {
        ByteConverter c = new ByteConverter();
        List<Byte> result = c.convert(Byte.class, null, null, (String[]) null);
        assertTrue(result.isEmpty());
    }

    @Test //
    public void testEmptyArray() throws Exception {
        ByteConverter c = new ByteConverter();
        List<Byte> result = c.convert(Byte.class, null, null, new String[0]);
        assertTrue(result.isEmpty());
    }
}
