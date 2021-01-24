package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.cactusthorn.routing.RoutingInitializationException;

public class StringConstructorConverter implements Converter {

    private final Map<Class<?>, Constructor<?>> constructors = new HashMap<>();

    @Override //
    public Object convert(Class<?> type, Type genericType, Annotation[] annotations, String value)
            throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (value == null) {
            return null;
        }
        return constructors.get(type).newInstance(value);
    }

    boolean register(Class<?> type) {
        Optional<Constructor<?>> constructor = findConstructor(type);
        if (constructor.isPresent()) {
            constructors.put(type, constructor.get());
            return true;
        }
        return false;
    }

    private static Optional<Constructor<?>> findConstructor(Class<?> clazz) {
        try {
            for (Constructor<?> ctor : clazz.getConstructors()) {
                Class<?>[] pType = ctor.getParameterTypes();
                if (pType != null && pType.length == 1 && pType[0] == String.class) {
                    return Optional.of(ctor);
                }
            }
            return Optional.empty();
        } catch (SecurityException e) {
            throw new RoutingInitializationException("The problem with method invocation", e);
        }
    }
}
