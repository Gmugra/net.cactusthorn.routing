package net.cactusthorn.routing.delegate;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import net.cactusthorn.routing.Http;

public class CookieHeaderDelegate extends HeaderDelegateAncestor implements HeaderDelegate<Cookie> {

    @Override //
    public Cookie fromString(String str) {
        if (str == null) {
            throw new IllegalArgumentException("parameter can not be null");
        }
        return Http.parseCookies(str).get(0);
    }

    @Override //
    public String toString(Cookie cookie) {
        if (cookie == null) {
            throw new IllegalArgumentException("cookie can not be null");
        }

        String result = "$Version=" + cookie.getVersion() + ';';

        result += cookie.getName() + '=' + addQuotesIfContainsWhitespace(cookie.getValue());

        if (cookie.getDomain() != null) {
            result += ";$Domain=" + addQuotesIfContainsWhitespace(cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            result += ";$Path=" + addQuotesIfContainsWhitespace(cookie.getPath());
        }
        return result;
    }
}
