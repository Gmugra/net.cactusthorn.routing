package net.cactusthorn.routing.delegate;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import net.cactusthorn.routing.util.Headers;

public class CookieHeaderDelegate implements HeaderDelegate<Cookie> {

    @Override //
    public Cookie fromString(String str) {
        if (str == null) {
            throw new IllegalArgumentException("parameter can not be null");
        }
        return Headers.parseCookies(str).get(0);
    }

    @Override //
    public String toString(Cookie cookie) {
        if (cookie == null) {
            throw new IllegalArgumentException("cookie can not be null");
        }

        StringBuilder result = new StringBuilder("$Version=").append(cookie.getVersion()).append(';');
        result.append(cookie.getName()).append('=');
        Headers.addQuotesIfContainsWhitespace(result, cookie.getValue());

        if (cookie.getDomain() != null) {
            result.append(";$Domain=");
            Headers.addQuotesIfContainsWhitespace(result, cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            result.append(";$Path=");
            Headers.addQuotesIfContainsWhitespace(result, cookie.getPath());
        }
        return result.toString();
    }
}
