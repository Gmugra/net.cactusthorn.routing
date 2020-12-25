package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;

public class StaticStringMethodConverterTest {

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
        StaticStringMethodConverter c = new StaticStringMethodConverter("valueOf");
        assertTrue(c.register(Integer.class));
        assertFalse(c.register(X.class));
    }

    @Test //
    public void invoke() throws ConverterException {
        StaticStringMethodConverter c = new StaticStringMethodConverter("valueOf");
        c.register(Integer.class);
        assertEquals(123, c.convert(null, Integer.class, "123"));
    }

    @Test //
    public void wrong() {
        StaticStringMethodConverter c = new StaticStringMethodConverter("valueOf");
        c.register(Integer.class);
        assertThrows(ConverterException.class, () -> c.convert(Integer.class, "12dd"));
    }

    @Test //
    public void nullValue() throws ConverterException {
        StaticStringMethodConverter c = new StaticStringMethodConverter("valueOf");
        c.register(Integer.class);
        @SuppressWarnings("unused") Integer i = (Integer) c.convert(Integer.class, (String) null);
    }

    @Test //
    public void fromString() throws ConverterException {
        StaticStringMethodConverter c = new StaticStringMethodConverter("fromString");
        c.register(UUID.class);
        UUID uuid = (UUID) c.convert(UUID.class, "46400000-8cc0-11bd-b43e-10d46e4ef14d");
        assertEquals("46400000-8cc0-11bd-b43e-10d46e4ef14d", uuid.toString());
    }
}
