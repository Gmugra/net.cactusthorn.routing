package net.cactusthorn.routing;

import javax.servlet.ServletContext;

public interface ComponentProvider {

    default void init(ServletContext servletContext) {
    }

    Object provide(Class<?> clazz);
}
