package net.cactusthorn.routing;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public interface ComponentProvider {

    default void init(ServletContext servletContext) {
    }

    Object provide(Class<?> clazz, HttpServletRequest request);
}
