package net.cactusthorn.routing.delegate;

import java.util.Locale;

import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import net.cactusthorn.routing.util.Headers;
import net.cactusthorn.routing.util.Language;
import net.cactusthorn.routing.util.Messages;

public class LanguageHeaderDelegate implements HeaderDelegate<Language> {

    @Override //
    public Language fromString(String languageTag) {
        if (languageTag == null) {
            throw new IllegalArgumentException(Messages.isNull("languageTag"));
        }
        String[] parts = languageTag.split(";");
        String localeStr = parts[0].trim();
        Locale locale = null;
        if ("*".equals(localeStr)) {
            locale = Locale.forLanguageTag(localeStr);
        } else {
            locale = new Locale.Builder().setLanguageTag(localeStr).build();
        }
        String q = null;
        if (parts.length > 1) {
            String[] subParts = Headers.getSubParts(parts[1]);
            if ("q".equals(subParts[0])) {
                q = subParts[1];
            }
        }
        return new Language(locale, q);
    }

    @Override //
    public String toString(Language language) {
        if (language == null) {
            throw new IllegalArgumentException(Messages.isNull("language"));
        }
        return language.getLocale().toLanguageTag() + ";q=" + language.getQ();
    }

}
