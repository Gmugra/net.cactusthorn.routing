package net.cactusthorn.routing.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

public class HeadersTest {

    @Test //
    public void languageSort() {

        List<Language> list = new ArrayList<>();
        list.add(new Language(Locale.GERMANY, "0.5"));
        list.add(new Language(Locale.FRANCE));
        list.add(null);
        list.add(null);
        list.add(new Language(Locale.US));
        list.add(new Language(Locale.UK, "0.875"));
        list.add(null);

        Collections.sort(list, Headers.ACCEPT_LANGUAGE_COMPARATOR);

        assertEquals(Locale.FRANCE, list.get(0).getLocale());
        assertEquals(Locale.US, list.get(1).getLocale());
        assertEquals(Locale.UK, list.get(2).getLocale());
        assertEquals(Locale.GERMANY, list.get(3).getLocale());
        assertNull(list.get(4));
        assertNull(list.get(5));
        assertNull(list.get(6));
    }

    @Test //
    public void parseEmptyAcceptLanguage() {
        List<Locale> locales = Headers.parseAcceptLanguage(null);
        assertTrue(locales.isEmpty());
        locales = Headers.parseAcceptLanguage("  ");
        assertTrue(locales.isEmpty());
        locales = Headers.parseAcceptLanguage("*");
        assertEquals("", locales.get(0).toString());
    }

    @Test //
    public void parseAcceptLanguage() {
        List<Locale> locales = Headers.parseAcceptLanguage("fr-CH, en;q=0.8, de;q=0.7, *;q=0.5, fr;q=0.9");
        assertEquals(5, locales.size());
        assertEquals(new Locale("fr","CH"), locales.get(0));
        assertEquals(new Locale("fr"), locales.get(1));
        assertEquals(new Locale("en"), locales.get(2));
        assertEquals(new Locale("de"), locales.get(3));
        assertEquals(Locale.forLanguageTag("*"), locales.get(4));
    }
}
