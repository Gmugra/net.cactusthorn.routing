package net.cactusthorn.routing.util;

import java.util.Locale;

import javax.ws.rs.ext.RuntimeDelegate;

public class Language {

    private Locale locale;
    private String q;

    public Language(Locale locale) {
        this.locale = locale;
    }

    public Language(Locale locale, String q) {
        this.locale = locale;
        this.q = q;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getQ() {
        if (q == null) {
            return "1.0";
        }
        return q;
    }

    public static Language valueOf(String language) {
        return RuntimeDelegate.getInstance().createHeaderDelegate(Language.class).fromString(language);
    }

    @Override public String toString() {
        return RuntimeDelegate.getInstance().createHeaderDelegate(Language.class).toString(this);
    }
}
