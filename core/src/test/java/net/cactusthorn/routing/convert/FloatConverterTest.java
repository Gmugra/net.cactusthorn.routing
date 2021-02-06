package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class FloatConverterTest {

    @Test //
    public void test() {
        FloatConverter c = new FloatConverter();
        Float result = c.convert(Float.class, null, null, "125.5");
        assertEquals(125.5f, result);
        result = c.convert(null, null, null, (String) null);
        assertNull(result);
        result = c.convert(null, null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Throwable {
        FloatConverter c = new FloatConverter();
        String[] value = new String[] { "125.5", "  ", "16.3" };
        Float[] valuesAsFloat = new Float[] { 125.5f, null, 16.3f };
        List<Float> result = c.convert(Float.class, null, null, value);
        assertArrayEquals(valuesAsFloat, result.toArray());
    }

    @Test //
    public void testNullArray() throws Throwable {
        FloatConverter c = new FloatConverter();
        List<Float> result = c.convert(Float.class, null, null, (String[]) null);
        assertTrue(result.isEmpty());
    }

    @Test //
    public void testEmptyArray() throws Throwable {
        FloatConverter c = new FloatConverter();
        List<Float> result = c.convert(Float.class, null, null, new String[0]);
        assertTrue(result.isEmpty());
    }
}
