package net.cactusthorn.routing.delegate;

import java.util.Locale;

import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public final class LocaleHeaderDelegate implements HeaderDelegate<Locale> {

    @Override //
    public Locale fromString(String languageTag) {
        if (languageTag == null) {
            throw new IllegalArgumentException("languageTag can not be null");
        }
        return new Locale.Builder().setLanguageTag(languageTag.trim()).build();
    }

    @Override //
    public String toString(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException("locale can not be null");
        }
        return locale.toLanguageTag();
    }
}
