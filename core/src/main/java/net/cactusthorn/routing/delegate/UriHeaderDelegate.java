package net.cactusthorn.routing.delegate;

import java.net.URI;

import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public final class UriHeaderDelegate implements HeaderDelegate<URI> {

    @Override //
    public URI fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value can not be null");
        }
        return URI.create(value);
    }

    @Override //
    public String toString(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri can not be null");
        }
        return uri.toASCIIString();
    }
}
