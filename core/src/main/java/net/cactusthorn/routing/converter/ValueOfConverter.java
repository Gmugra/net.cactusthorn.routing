package net.cactusthorn.routing.converter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RoutingException;

public class ValueOfConverter implements Converter<Object> {

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

    public static boolean support(Class<?> clazz) {
        return findMethod(clazz) != null;
    }

    private Method method;

    public ValueOfConverter(Class<?> clazz) {
        method = findMethod(clazz);
    }

    @Override //
    public Object convert(HttpServletRequest req, HttpServletResponse res, ServletContext con, String input) {
        try {
            if (input == null) {
                return null;
            }
            return method.invoke(null, input);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RoutingException("The problem with method invocation", e);
        }
    }

}
