package net.cactusthorn.routing.delegate;

import java.util.Map;
import java.util.List;
import java.util.StringJoiner;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import net.cactusthorn.routing.util.Headers;
import net.cactusthorn.routing.util.Messages;

public class CacheControlHeaderDelegate implements HeaderDelegate<CacheControl> {

    @Override
    public CacheControl fromString(String header) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString(CacheControl cacheControl) {
        if (cacheControl == null) {
            throw new IllegalArgumentException(Messages.isNull("cacheControl"));
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
            joiner.add("max-age=" + cacheControl.getMaxAge());
        }
        if (cacheControl.getSMaxAge() >= 0) {
            joiner.add("s-maxage=" + cacheControl.getSMaxAge());
        }
        for (Map.Entry<String, String> entry : cacheControl.getCacheExtension().entrySet()) {
            joiner.add(entry.getKey() + '=' + Headers.addQuotesIfContainsWhitespace(entry.getValue()));
        }
        return joiner.toString();
    }

    private String withFields(String name, List<String> fields) {
        if (fields.isEmpty()) {
            return name;
        }
        StringJoiner joiner = new StringJoiner(", ", name + "=\"", "\"");
        fields.forEach(f -> joiner.add(f));
        return joiner.toString();
    }
}
