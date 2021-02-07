package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.Cookie;

import net.cactusthorn.routing.delegate.CookieHeaderDelegate;

public class CookieConverter implements Converter<Cookie> {

    private static final CookieHeaderDelegate DELEGATE = new CookieHeaderDelegate();

    @Override //
    public Cookie convert(Class<?> type, Type genericType, Annotation[] annotations, String value) throws Throwable {
        if (value == null) {
            return null;
        }
        return DELEGATE.fromString(value);
    }
}
