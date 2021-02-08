package net.cactusthorn.routing.delegate;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

import net.cactusthorn.routing.util.Headers;

public final class LinkImpl extends Link {

    private URI uri;
    private Map<String, String> params;

    private LinkImpl(URI uri, Map<String, String> params) {
        this.uri = uri;
        this.params = Collections.unmodifiableMap(params);
    }

    @Override public URI getUri() {
        return uri;
    }

    @Override public UriBuilder getUriBuilder() {
        throw new UnsupportedOperationException();
    }

    @Override public String getRel() {
        return params.get(REL);
    }

    @Override public List<String> getRels() {
        String rel = params.get(REL);
        if (rel == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(Arrays.asList(rel.split(" ")));
    }

    @Override public String getTitle() {
        return params.get(TITLE);
    }

    @Override public String getType() {
        return params.get(TYPE);
    }

    @Override public Map<String, String> getParams() {
        return params;
    }

    @Override public String toString() {
        StringBuilder result = new StringBuilder("<").append(getUri().toASCIIString()).append('>');
        for (Map.Entry<String, String> entry : getParams().entrySet()) {
            result.append("; ").append(entry.getKey()).append('=');
            Headers.addQuotesIfContainsWhitespace(result, entry.getValue());
        }
        return result.toString();
    }

    public static class LinkBuilderImpl implements Builder {

        private URI uuri;
        private final Map<String, String> params = new LinkedHashMap<>();

        @Override public Builder link(Link link) {
            this.uuri = link.getUri();
            params.clear();
            params.putAll(link.getParams());
            return this;
        }

        @Override public Builder link(String link) {
            String tmp = link.trim();
            if (tmp.charAt(0) != '<') {
                throw new IllegalArgumentException("Wrong: '<' is missing");
            }

            String[] parts = tmp.split(";");
            String uriAsStr = parts[0].trim();
            if (tmp.charAt(uriAsStr.length() - 1) != '>') {
                throw new IllegalArgumentException("Wrong: '>' is missing");
            }
            uuri = URI.create(uriAsStr.substring(1, uriAsStr.length() - 1).trim());

            params.clear();
            for (int i = 1; i < parts.length; i++) {
                String[] subParts = Headers.getSubParts(parts[i]);
                params.put(subParts[0], subParts[1]);
            }

            return this;
        }

        @Override public Builder uri(URI uri) {
            this.uuri = uri;
            return this;
        }

        @Override public Builder uri(String uri) {
            this.uuri = URI.create(uri);
            return this;
        }

        @Override public Builder baseUri(URI uri) {
            throw new UnsupportedOperationException();
        }

        @Override public Builder baseUri(String uri) {
            throw new UnsupportedOperationException();
        }

        @Override public Builder uriBuilder(UriBuilder uriBuilder) {
            throw new UnsupportedOperationException();
        }

        @Override public Builder rel(String rel) {
            params.put(REL, rel);
            return this;
        }

        @Override public Builder title(String title) {
            params.put(TITLE, title);
            return this;
        }

        @Override public Builder type(String type) {
            params.put(TYPE, type);
            return this;
        }

        @Override public Builder param(String name, String value) {
            params.put(name, value);
            return this;
        }

        @Override public Link build(Object... values) {
            return new LinkImpl(uuri, params);
        }

        @Override public Link buildRelativized(URI uri, Object... vvalues) {
            throw new UnsupportedOperationException();
        }
    }
}
