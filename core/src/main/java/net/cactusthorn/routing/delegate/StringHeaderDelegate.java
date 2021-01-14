package net.cactusthorn.routing.delegate;

import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public final class StringHeaderDelegate implements HeaderDelegate<String> {

    @Override //
    public String fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value can not be null");
        }
        return value;
    }

    @Override //
    public String toString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("String can not be null");
        }
        return value;
    }

}
