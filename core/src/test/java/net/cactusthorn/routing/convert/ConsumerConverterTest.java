package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.Consumer;

public class ConsumerConverterTest {

    public static final Consumer TEST_CONSUMER = (clazz, mediaType, data) -> {
        return new java.util.Date();
    };

    public static final Consumer EXCEPTION_CONSUMER = (clazz, mediaType, data) -> {
        throw new RuntimeException("TEST IT");
    };

    @Test //
    public void array() {
        ConsumerConverter c = new ConsumerConverter(new MediaType("aa","bb"), TEST_CONSUMER);
        assertThrows(UnsupportedOperationException.class, () -> c.convert(java.util.Date.class, new String[] { "a" }));
    }

    @Test //
    public void error() {
        ConsumerConverter c = new ConsumerConverter(new MediaType("aa","bb"), EXCEPTION_CONSUMER);
        assertThrows(RuntimeException.class, () -> c.convert(null, java.util.Date.class, (String)null));
    }
}
