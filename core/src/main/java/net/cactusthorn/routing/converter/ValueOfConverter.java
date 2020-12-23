package net.cactusthorn.routing.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import net.cactusthorn.routing.RoutingException;

public class ValueOfConverter implements Converter<Object> {

    private final Map<Class<?>, Method> classes = new HashMap<>();

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String value) {
        try {
            if (value == null) {
                return null;
            }
            Method method = classes.get(type);
            return method.invoke(null, value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RoutingException("The problem with method invocation", e);
        }
    }

    public boolean register(Class<?> type) {
        Method method = findMethod(type);
        if (method != null) {
            classes.put(type, method);
            return true;
        }
        return false;
    }

    private static Method findMethod(Class<?> clazz) {
        try {
            Method method = clazz.getMethod("valueOf", String.class);
            return Modifier.isStatic(method.getModifiers()) ? method : null;
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e) {
            throw new RoutingException("The problem with method invocation", e);
        }
    }

}
