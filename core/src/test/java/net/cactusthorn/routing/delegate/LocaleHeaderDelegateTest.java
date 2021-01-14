package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.*;

import java.util.IllformedLocaleException;
import java.util.Locale;

import org.junit.jupiter.api.Test;

public class LocaleHeaderDelegateTest {

    private static final LocaleHeaderDelegate DELEGATE = new LocaleHeaderDelegate();

    @Test
    public void fromSring() {
        Locale locale = DELEGATE.fromString("aa-BB");
        assertEquals(new Locale("aa", "BB"), locale);
    }

    @Test
    public void fromOnlyLang() {
        Locale locale = DELEGATE.fromString("aa");
        assertEquals(new Locale("aa"), locale);
    }

    @Test
    public void wrongFrom() {
        assertThrows(IllformedLocaleException.class, () -> DELEGATE.fromString("en- GB"));
    }

    @Test
    public void fromTrim() {
        Locale locale = DELEGATE.fromString("   zz-KK  ");
        assertEquals(new Locale("zz", "KK"), locale);
    }

    @Test
    public void fromNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString(null));
    }

    @Test
    public void toNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.toString(null));
    }

    @Test
    public void _toString() {
        Locale locale = new Locale("aa", "BB");
        String languageTag = DELEGATE.toString(locale);
        assertEquals("aa-BB", languageTag);
    }
}
