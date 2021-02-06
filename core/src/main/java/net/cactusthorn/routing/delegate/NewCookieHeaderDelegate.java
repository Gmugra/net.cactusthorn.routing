package net.cactusthorn.routing.delegate;

import java.util.Date;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public final class NewCookieHeaderDelegate implements HeaderDelegate<NewCookie> {

    private static final DateHeaderDelegate DATE_HEADER_DELEGATE = new DateHeaderDelegate();

    @Override //
    public NewCookie fromString(String str) {
        if (str == null) {
            throw new IllegalArgumentException("parameter can not be null");
        }

        String[] parts = str.trim().split(";");

        String[] main = Headers.getSubParts(parts[0].trim());

        String name = main[0];
        String value = main[1];
        String path = null;
        String domain = null;
        int version = NewCookie.DEFAULT_VERSION;
        String comment = null;
        int maxAge = NewCookie.DEFAULT_MAX_AGE;
        Date expiry = null;
        boolean secure = false;
        boolean httpOnly = false;

        for (int i = 1; i < parts.length; i++) {
            String tmp = parts[i].trim();
            if (tmp.startsWith("Version")) {
                String[] subPart = Headers.getSubParts(tmp.trim());
                version = Integer.parseInt(subPart[1]);
            } else if (tmp.startsWith("Comment")) {
                String[] subPart = Headers.getSubParts(tmp.trim());
                comment = subPart[1];
            } else if (tmp.startsWith("Domain")) {
                String[] subPart = Headers.getSubParts(tmp.trim());
                domain = subPart[1];
            } else if (tmp.startsWith("Path")) {
                String[] subPart = Headers.getSubParts(tmp.trim());
                path = subPart[1];
            } else if (tmp.startsWith("Max-Age")) {
                String[] subPart = Headers.getSubParts(tmp.trim());
                maxAge = Integer.parseInt(subPart[1]);
            } else if ("Secure".equals(tmp)) {
                secure = true;
            } else if ("HttpOnly".equals(tmp)) {
                httpOnly = true;
            } else if (tmp.startsWith("Expires")) {
                String[] subPart = Headers.getSubParts(tmp.trim());
                expiry = DATE_HEADER_DELEGATE.fromString(subPart[1]);
            }
        }

        return new NewCookie(name, value, path, domain, version, comment, maxAge, expiry, secure, httpOnly);
    }

    @Override //
    public String toString(NewCookie cookie) {
        if (cookie == null) {
            throw new IllegalArgumentException("cookie can not be null");
        }

        StringBuilder result = new StringBuilder(cookie.getName()).append('=');
        Headers.addQuotesIfContainsWhitespace(result, cookie.getValue());
        result.append(";Version=").append(cookie.getVersion());

        if (cookie.getComment() != null) {
            result.append(";Comment=");
            Headers.addQuotesIfContainsWhitespace(result, cookie.getComment());
        }
        if (cookie.getDomain() != null) {
            result.append(";Domain=");
            Headers.addQuotesIfContainsWhitespace(result, cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            result.append(";Path=");
            Headers.addQuotesIfContainsWhitespace(result, cookie.getPath());
        }
        if (cookie.getMaxAge() != -1) {
            result.append(";Max-Age=").append(cookie.getMaxAge());
        }
        if (cookie.isSecure()) {
            result.append(";Secure");
        }
        if (cookie.isHttpOnly()) {
            result.append(";HttpOnly");
        }
        if (cookie.getExpiry() != null) {
            result.append(";Expires=").append(DATE_HEADER_DELEGATE.toString(cookie.getExpiry()));
        }
        return result.toString();
    }
}
