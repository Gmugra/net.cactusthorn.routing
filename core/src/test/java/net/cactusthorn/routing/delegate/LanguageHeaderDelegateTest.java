package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class LanguageHeaderDelegateTest {

    private static final LanguageHeaderDelegate DELEGATE = new LanguageHeaderDelegate();

    @Test
    public void fromNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString(null));
    }

    @Test
    public void toNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.toString(null));
    }
}
