package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class FloatConverterTest {

    @Test //
    public void test() throws Exception {
        FloatConverter c = new FloatConverter();
        Float result = (Float) c.convert(Float.class, null, null, "125.5");
        assertEquals(125.5f, result);
        result = (Float) c.convert(null, null, null, (String) null);
        assertNull(result);
        result = (Float) c.convert(null, null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Exception {
        FloatConverter c = new FloatConverter();
        String[] value = new String[] { "125.5", "  ", "16.3" };
        Float[] valuesAsFloat = new Float[] { 125.5f, null, 16.3f };
        Float[] result = (Float[]) c.convert(Float.class, null, null, value);
        assertArrayEquals(valuesAsFloat, result);
    }

    @Test //
    public void testNullArray() throws Exception {
        FloatConverter c = new FloatConverter();
        Object result = (Object) c.convert(Float.class, null, null, (String[]) null);
        assertNull(result);
    }
}
