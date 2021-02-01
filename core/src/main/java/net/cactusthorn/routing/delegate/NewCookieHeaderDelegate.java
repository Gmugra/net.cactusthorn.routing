package net.cactusthorn.routing.delegate;

import java.util.Date;

import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public final class NewCookieHeaderDelegate extends HeaderDelegateAncestor implements HeaderDelegate<NewCookie> {

    private static final DateHeaderDelegate DATE_HEADER_DELEGATE = new DateHeaderDelegate();

    @Override //
    public NewCookie fromString(String str) {
        if (str == null) {
            throw new IllegalArgumentException("parameter can not be null");
        }

        String[] parts = str.trim().split(";");

        String[] main = getSubParts(parts[0].trim());

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
                String[] subPart = getSubParts(tmp.trim());
                version = Integer.parseInt(subPart[1]);
            } else if (tmp.startsWith("Comment")) {
                String[] subPart = getSubParts(tmp.trim());
                comment = subPart[1];
            } else if (tmp.startsWith("Domain")) {
                String[] subPart = getSubParts(tmp.trim());
                domain = subPart[1];
            } else if (tmp.startsWith("Path")) {
                String[] subPart = getSubParts(tmp.trim());
                path = subPart[1];
            } else if (tmp.startsWith("Max-Age")) {
                String[] subPart = getSubParts(tmp.trim());
                maxAge = Integer.parseInt(subPart[1]);
            } else if ("Secure".equals(tmp)) {
                secure = true;
            } else if ("HttpOnly".equals(tmp)) {
                httpOnly = true;
            } else if (tmp.startsWith("Expires")) {
                String[] subPart = getSubParts(tmp.trim());
                expiry = DATE_HEADER_DELEGATE.fromString(subPart[1]);
            }
        }

        return new NewCookie(name, value, path, domain, version, comment, maxAge, expiry, secure, httpOnly);
    }

    private String[] getSubParts(String str) {
        int valueStart = str.indexOf('=');
        if (valueStart == -1) {
            throw new IllegalArgumentException("Wrong: '=' is missing");
        }
        String value = str.substring(valueStart + 1).trim();
        if (value.charAt(0) == '"') {
            value = value.substring(1, value.length() - 1).trim();
        }
        return new String[] {str.substring(0, valueStart).trim(), value};
    }

    @Override //
    public String toString(NewCookie cookie) {
        if (cookie == null) {
            throw new IllegalArgumentException("cookie can not be null");
        }

        String result = cookie.getName() + '=' + addQuotesIfContainsWhitespace(cookie.getValue());
        result += ";Version=" + cookie.getVersion();

        if (cookie.getComment() != null) {
            result += ";Comment=" + addQuotesIfContainsWhitespace(cookie.getComment());
        }
        if (cookie.getDomain() != null) {
            result += ";Domain=" + addQuotesIfContainsWhitespace(cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            result += ";Path=" + addQuotesIfContainsWhitespace(cookie.getPath());
        }
        if (cookie.getMaxAge() != -1) {
            result += ";Max-Age=" + cookie.getMaxAge();
        }
        if (cookie.isSecure()) {
            result += ";Secure";
        }
        if (cookie.isHttpOnly()) {
            result += ";HttpOnly";
        }
        if (cookie.getExpiry() != null) {
            result += ";Expires=" + DATE_HEADER_DELEGATE.toString(cookie.getExpiry());
        }
        return result;
    }
}
