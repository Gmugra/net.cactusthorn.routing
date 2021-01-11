package net.cactusthorn.routing.convert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.cactusthorn.routing.RoutingInitializationException;

public final class StaticStringMethodConverter implements Converter {

    private final Map<Class<?>, Method> methods = new HashMap<>();
    private String methodName;

    public StaticStringMethodConverter(String methodName) {
        this.methodName = methodName;
    }

    @Override //
    public Object convert(Class<?> type, String value)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (value == null) {
            return null;
        }
        Method method = methods.get(type);
        return method.invoke(null, value);
    }

    boolean register(Class<?> type) {
        Optional<Method> method = findMethod(type);
        if (method.isPresent()) {
            methods.put(type, method.get());
            return true;
        }
        return false;
    }

    private Optional<Method> findMethod(Class<?> clazz) {
        try {
            Method method = clazz.getMethod(methodName, String.class);
            return Modifier.isStatic(method.getModifiers()) ? Optional.of(method) : Optional.empty();
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        } catch (SecurityException e) {
            throw new RoutingInitializationException("The problem with method invocation", e);
        }
    }

}
