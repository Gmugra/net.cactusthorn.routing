package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.Test;

public class UriHeaderDelegateTest {

    private static final UriHeaderDelegate DELEGATE = new UriHeaderDelegate();

    @Test
    public void fromString() {
        URI uri = DELEGATE.fromString("aa/bb/cc?a=\u00C4");
        assertEquals("aa/bb/cc?a=%C3%84", DELEGATE.toString(uri));
    }

    @Test
    public void wrong() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString("aa/bb /cc?a=\u00C4"));
    }

    @Test
    public void fromNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString(null));
    }

    @Test
    public void toNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.toString(null));
    }
}
