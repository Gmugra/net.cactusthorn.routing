package net.cactusthorn.routing.delegate;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public final class EntityTagHeaderDelegate implements HeaderDelegate<EntityTag> {

    @Override public EntityTag fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value can not be null");
        }
        String tmp = value.trim();
        boolean weak = tmp.startsWith("W/");
        if (weak) {
            tmp = tmp.substring(2);
        }
        tmp = tmp.substring(1, tmp.length() - 1);
        return new EntityTag(tmp, weak);
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
