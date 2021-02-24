package net.cactusthorn.routing.delegate;

import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import net.cactusthorn.routing.util.Messages;

public final class StringHeaderDelegate implements HeaderDelegate<String> {

    @Override //
    public String fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException(Messages.isNull("value"));
        }
        return value;
    }

    @Override //
    public String toString(String value) {
        if (value == null) {
            throw new IllegalArgumentException(Messages.isNull("value"));
        }
        return value;
    }

}
