package net.cactusthorn.routing;

import javax.servlet.ServletContext;

public interface Consumer {

    default void init(ServletContext servletContext, ComponentProvider componentProvider) {
    }

    Object consume(Class<?> clazz, String mediaType, RequestData data);
}
