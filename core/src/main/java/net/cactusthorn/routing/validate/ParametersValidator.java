package net.cactusthorn.routing.validate;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import net.cactusthorn.routing.ComponentProvider;

public interface ParametersValidator {

    default void init(ServletContext servletContext, ComponentProvider componentProvider) {
    }

    void validate(Object object, Method method, Object[] parameters);
}
