package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
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
    public void invoke() throws Exception {
        StaticStringMethodConverter c = new StaticStringMethodConverter("valueOf");
        c.register(Integer.class);
        assertEquals(123, c.convert(Integer.class, null, null, "123"));
    }

    @Test //
    public void wrong() throws Exception {
        StaticStringMethodConverter c = new StaticStringMethodConverter("valueOf");
        c.register(Integer.class);
        Exception exception = assertThrows(InvocationTargetException.class, () -> c.convert(Integer.class, null, null, "12dd"));
        assertEquals(NumberFormatException.class, exception.getCause().getClass());
    }

    @Test //
    public void nullValue() throws Exception {
        StaticStringMethodConverter c = new StaticStringMethodConverter("valueOf");
        c.register(Integer.class);
        @SuppressWarnings("unused") Integer i = (Integer) c.convert(Integer.class, null, null, (String) null);
    }

    @Test //
    public void fromString() throws Exception {
        StaticStringMethodConverter c = new StaticStringMethodConverter("fromString");
        c.register(UUID.class);
        UUID uuid = (UUID) c.convert(UUID.class, null, null, "46400000-8cc0-11bd-b43e-10d46e4ef14d");
        assertEquals("46400000-8cc0-11bd-b43e-10d46e4ef14d", uuid.toString());
    }
}
