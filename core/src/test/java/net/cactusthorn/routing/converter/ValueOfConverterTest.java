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
        ValueOfConverter c = new ValueOfConverter();
        assertTrue(c.register(Integer.class));
        assertFalse(c.register(X.class));
    }

    @Test //
    public void invoke() {
        ValueOfConverter c = new ValueOfConverter();
        c.register(Integer.class);
        assertEquals(123, c.convert(null, Integer.class, "123"));
    }

    @Test //
    public void wrong() {
        ValueOfConverter c = new ValueOfConverter();
        c.register(Integer.class);
        assertThrows(RoutingException.class, () -> c.convert(null, Integer.class, "12dd"));
    }

    @Test //
    public void nullValue() {
        ValueOfConverter c = new ValueOfConverter();
        c.register(Integer.class);
        @SuppressWarnings("unused") Integer i = (Integer) c.convert(null, Integer.class, null);
    }
}
