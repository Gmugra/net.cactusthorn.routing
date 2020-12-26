package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ByteConverterTest {

    @Test //
    public void test() throws ConverterException {
        ByteConverter c = new ByteConverter();
        Byte result = (Byte) c.convert(Byte.class, "125");
        assertEquals((byte)125, result);
        result = (Byte) c.convert(null, null, (String) null);
        assertNull(result);
        result = (Byte) c.convert(null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws ConverterException {
        ByteConverter c = new ByteConverter();
        String[] value = new String[] { "125", "  ", "16" };
        Byte[] valuesAsByte = new Byte[] { 125, null, 16 };
        Byte[] result = (Byte[]) c.convert(Byte.class, value);
        assertArrayEquals(valuesAsByte, result);
    }

    @Test //
    public void testNullArray() throws ConverterException {
        ByteConverter c = new ByteConverter();
        Object result = (Object) c.convert(Byte.class, (String[]) null);
        assertNull(result);
    }
}