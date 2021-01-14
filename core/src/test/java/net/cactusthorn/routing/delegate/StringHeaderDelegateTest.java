package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class StringHeaderDelegateTest {

    private final static StringHeaderDelegate DELEGATE = new StringHeaderDelegate();

    @Test //
    public void fromString() {
        String result = DELEGATE.fromString("TEST");
        assertEquals("TEST", result);
    }

    @Test //
    public void _toString() {
        String result = DELEGATE.toString("TEST");
        assertEquals("TEST", result);
    }

    @Test //
    public void nullValue() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.toString(null));
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString(null));
    }
}
