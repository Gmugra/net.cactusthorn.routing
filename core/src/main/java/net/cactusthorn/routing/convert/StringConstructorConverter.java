package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class StringConstructorConverter implements Converter<Object> {

    private static final MethodType METHOD_TYPE = MethodType.methodType(void.class, String.class);

    private final Map<Type, MethodHandle> constructors = new HashMap<>();

    @Override //
    public Object convert(Class<?> type, Type genericType, Annotation[] annotations, String value) throws Throwable {
        if (value == null) {
            return null;
        }
        return constructors.get(type).invoke(value);
    }

    boolean register(Class<?> type) {
        try {
            MethodHandle methodHandle = MethodHandles.publicLookup().findConstructor(type, METHOD_TYPE);
            constructors.put(type, methodHandle);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
