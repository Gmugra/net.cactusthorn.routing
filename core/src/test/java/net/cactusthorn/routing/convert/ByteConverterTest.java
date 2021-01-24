package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class ByteConverterTest {

    @Test //
    public void test() throws Exception {
        ByteConverter c = new ByteConverter();
        Byte result = (Byte) c.convert(Byte.class, null, null, "125");
        assertEquals((byte)125, result);
        result = (Byte) c.convert(null, null, null, (String) null);
        assertNull(result);
        result = (Byte) c.convert(null, null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Exception {
        ByteConverter c = new ByteConverter();
        String[] value = new String[] { "125", "  ", "16" };
        Byte[] valuesAsByte = new Byte[] { 125, null, 16 };
        Byte[] result = (Byte[]) c.convert(Byte.class, null, null, value);
        assertArrayEquals(valuesAsByte, result);
    }

    @Test //
    public void testNullArray() throws Exception {
        ByteConverter c = new ByteConverter();
        Object result = (Object) c.convert(Byte.class, null, null, (String[]) null);
        assertNull(result);
    }
}
