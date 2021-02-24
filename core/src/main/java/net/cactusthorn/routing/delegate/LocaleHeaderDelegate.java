package net.cactusthorn.routing.delegate;

import java.util.Locale;

import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import net.cactusthorn.routing.util.Messages;

public final class LocaleHeaderDelegate implements HeaderDelegate<Locale> {

    @Override //
    public Locale fromString(String languageTag) {
        if (languageTag == null) {
            throw new IllegalArgumentException(Messages.isNull("languageTag"));
        }
        return new Locale.Builder().setLanguageTag(languageTag.trim()).build();
    }

    @Override //
    public String toString(Locale locale) {
        if (locale == null) {
            throw new IllegalArgumentException(Messages.isNull("locale"));
        }
        return locale.toLanguageTag();
    }
}
