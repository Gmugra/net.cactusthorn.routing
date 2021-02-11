package net.cactusthorn.routing.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Locale;

import org.junit.jupiter.api.Test;

public class LanguageTest {

    @Test //
    public void simple() {
        Language language = new Language(Locale.GERMANY);
        assertEquals("de-DE;q=1.0", language.toString());
    }

    @Test //
    public void q() {
        Language language = new Language(Locale.GERMAN, "0.3");
        assertEquals("de;q=0.3", language.toString());
    }

    @Test //
    public void valueOf() {
        Language language = Language.valueOf(" de-DE ; q=0.3 ");
        assertEquals(Locale.GERMANY, language.getLocale());
        assertEquals("0.3", language.getQ());
    }
}
