package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class StaticStringMethodConverter implements Converter<Object> {

    private final Map<Type, MethodHandle> methods = new HashMap<>();
    private String methodName;

    public StaticStringMethodConverter(String methodName) {
        this.methodName = methodName;
    }

    @Override //
    public Object convert(Class<?> type, Type genericType, Annotation[] annotations, String value) throws Throwable {
        if (value == null) {
            return null;
        }
        return methods.get(type).invoke(value);
    }

    boolean register(Class<?> type) {
        try {
            MethodType methodType = MethodType.methodType(type, String.class);
            MethodHandle methodHandle = MethodHandles.publicLookup().findStatic(type, methodName, methodType);
            methods.put(type, methodHandle);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
