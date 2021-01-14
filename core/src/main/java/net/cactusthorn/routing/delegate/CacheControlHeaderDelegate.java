package net.cactusthorn.routing.delegate;

import java.util.Map;
import java.util.List;
import java.util.StringJoiner;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public class CacheControlHeaderDelegate implements HeaderDelegate<CacheControl> {

    @Override
    public CacheControl fromString(String header) {
        throw new UnsupportedOperationException("CacheControl is only for response headers.");
    }

    @Override
    public String toString(CacheControl cacheControl) {
        if (cacheControl == null) {
            throw new IllegalArgumentException("CacheControl can not be null");
        }

        StringJoiner joiner = new StringJoiner(", ");
        if (!cacheControl.isPrivate()) {
            joiner.add("public");
        }
        if (cacheControl.isPrivate()) {
            joiner.add(withFields("private", cacheControl.getPrivateFields()));
        }
        if (cacheControl.isNoCache()) {
            joiner.add(withFields("no-cache", cacheControl.getNoCacheFields()));
        }
        if (cacheControl.isNoStore()) {
            joiner.add("no-store");
        }
        if (cacheControl.isNoTransform()) {
            joiner.add("no-transform");
        }
        if (cacheControl.isMustRevalidate()) {
            joiner.add("must-revalidate");
        }
        if (cacheControl.isProxyRevalidate()) {
            joiner.add("proxy-revalidate");
        }
        if (cacheControl.getMaxAge() >= 0) {
            joiner.add("max-age=" + Integer.toString(cacheControl.getMaxAge()));
        }
        if (cacheControl.getSMaxAge() >= 0) {
            joiner.add("s-maxage=" + Integer.toString(cacheControl.getSMaxAge()));
        }
        for (Map.Entry<String, String> entry : cacheControl.getCacheExtension().entrySet()) {
            joiner.add(entry.getKey() + '=' + addQuotesIfContainsWhitespace(entry.getValue()));
        }
        return joiner.toString();
    }

    private String withFields(String name, List<String> fields) {
        StringJoiner joiner = new StringJoiner(", ");
        fields.forEach(f -> joiner.add(f));
        String fieldsAsString = joiner.toString();
        if (fieldsAsString.isEmpty()) {
            return name;
        }
        return name + "=\"" + fieldsAsString + '\"';
    }

    private boolean containsWhiteSpace(String str) {
        for (char c : str.toCharArray()) {
            if (Character.isWhitespace(c)) {
                return true;
            }
        }
        return false;
    }

    private String addQuotesIfContainsWhitespace(String str) {
        if (containsWhiteSpace(str)) {
            return '"' + str + '"';
        }
        return str;
    }

}
