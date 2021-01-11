package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

public class DoubleConverterTest {

    @Test //
    public void test() throws Exception {
        DoubleConverter c = new DoubleConverter();
        Double result = (Double) c.convert(Double.class, "125.5");
        assertEquals(125.5d, result);
        result = (Double) c.convert(null, (String) null);
        assertNull(result);
        result = (Double) c.convert(null, "  ");
        assertNull(result);
    }

    @Test //
    public void testArray() throws Exception {
        DoubleConverter c = new DoubleConverter();
        String[] value = new String[] { "125.5", "  ", "16.3" };
        Double[] valuesAsDouble = new Double[] { 125.5d, null, 16.3d };
        Double[] result = (Double[]) c.convert(Double.class, value);
        assertArrayEquals(valuesAsDouble, result);
    }

    @Test //
    public void testNullArray() throws Exception {
        DoubleConverter c = new DoubleConverter();
        Object result = (Object) c.convert(Double.class, (String[]) null);
        assertNull(result);
    }
}
