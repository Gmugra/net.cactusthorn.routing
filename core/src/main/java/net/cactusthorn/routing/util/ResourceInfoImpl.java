package net.cactusthorn.routing.util;

import java.lang.reflect.Method;

import javax.ws.rs.container.ResourceInfo;

public final class ResourceInfoImpl implements ResourceInfo {

    private final Method method;
    private final Class<?> clazz;

    public ResourceInfoImpl(Class<?> clazz, Method method) {
        if (clazz == null) {
            throw new IllegalArgumentException(Messages.isNull("clazz"));
        }
        if (method == null) {
            throw new IllegalArgumentException(Messages.isNull("method"));
        }
        this.clazz = clazz;
        this.method = method;
    }

    @Override public Method getResourceMethod() {
        return method;
    }

    @Override public Class<?> getResourceClass() {
        return clazz;
    }

}
