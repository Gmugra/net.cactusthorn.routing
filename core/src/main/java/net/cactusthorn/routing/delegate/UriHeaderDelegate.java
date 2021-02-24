package net.cactusthorn.routing.delegate;

import java.net.URI;

import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import net.cactusthorn.routing.util.Messages;

public final class UriHeaderDelegate implements HeaderDelegate<URI> {

    @Override //
    public URI fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException(Messages.isNull("value"));
        }
        return URI.create(value);
    }

    @Override //
    public String toString(URI uri) {
        if (uri == null) {
            throw new IllegalArgumentException(Messages.isNull("uri"));
        }
        return uri.toASCIIString();
    }
}
