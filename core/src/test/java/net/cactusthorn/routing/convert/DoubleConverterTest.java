package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

public class DoubleConverterTest {

    @Test //
    public void test() throws Exception {
        DoubleConverter c = new DoubleConverter();
        Double result = c.convert(Double.class, null, null, "125.5");
        assertEquals(125.5d, result);
        result = c.convert(null, null, null, (String) null);
        assertNull(result);
        result = c.convert(null, null, null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Exception {
        DoubleConverter c = new DoubleConverter();
        String[] value = new String[] { "125.5", "  ", "16.3" };
        Double[] valuesAsDouble = new Double[] { 125.5d, null, 16.3d };
        List<Double> result = c.convert(Double.class, null, null, value);
        assertArrayEquals(valuesAsDouble, result.toArray());
    }

    @Test //
    public void testNullArray() throws Exception {
        DoubleConverter c = new DoubleConverter();
        List<Double> result = c.convert(Double.class, null, null, (String[]) null);
        assertTrue(result.isEmpty());
    }

    @Test //
    public void testEmptyArray() throws Exception {
        DoubleConverter c = new DoubleConverter();
        List<Double> result = c.convert(Double.class, null, null, new String[0]);
        assertTrue(result.isEmpty());
    }
}
