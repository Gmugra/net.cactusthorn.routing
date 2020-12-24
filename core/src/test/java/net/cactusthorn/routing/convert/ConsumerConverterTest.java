package net.cactusthorn.routing.convert;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.Consumer;

public class ConsumerConverterTest {

    public static final Consumer TEST_CONSUMER = (clazz, mediaType, data) -> {
        return new java.util.Date();
    };

    @Test //
    public void array() {
        ConsumerConverter c = new ConsumerConverter("aa/bb", TEST_CONSUMER);
        assertThrows(UnsupportedOperationException.class, () -> c.convert(java.util.Date.class, new String[] { "a" }));
    }
}
