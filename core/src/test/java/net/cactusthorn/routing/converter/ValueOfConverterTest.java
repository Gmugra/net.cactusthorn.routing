package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.RoutingException;

public class ValueOfConverterTest {

    public static class X {

        public X valueOf(String x) {
            return new X();
        }

        public String get(int i) {
            return "SSS";
        }
    }

    @Test //
    public void support() {
        assertTrue(ValueOfConverter.support(Integer.class));
        assertFalse(ValueOfConverter.support(X.class));
    }

    @Test //
    public void invoke() {
        ValueOfConverter v = new ValueOfConverter(Integer.class);
        assertEquals(123, v.convert(null, null, null, "123"));
    }

    @Test //
    public void wrong() {
        ValueOfConverter v = new ValueOfConverter(Integer.class);
        assertThrows(RoutingException.class, () -> v.convert(null, null, null, "12dd"));
    }

    @Test //
    public void nullValue() {
        ValueOfConverter v = new ValueOfConverter(Integer.class);
        @SuppressWarnings("unused") Integer i = (Integer) v.convert(null, null, null, null);
    }
}
