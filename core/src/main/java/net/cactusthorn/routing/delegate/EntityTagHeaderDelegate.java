package net.cactusthorn.routing.delegate;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public final class EntityTagHeaderDelegate implements HeaderDelegate<EntityTag> {

    @Override public EntityTag fromString(String str) {
        if (str == null) {
            throw new IllegalArgumentException("value can not be null");
        }
        String value = str.trim();
        boolean weak = value.startsWith("W/");
        if (weak) {
            value = value.substring(2);
        }
        value = value.substring(1, value.length() - 1);
        return new EntityTag(value, weak);
    }

    @Override public String toString(EntityTag entityTag) {
        if (entityTag == null) {
            throw new IllegalArgumentException("entityTag can not be null");
        }
        String header = "";
        if (entityTag.isWeak()) {
            header += "W/";
        }
        return header + '"' + entityTag.getValue() + '"';
    }
}
