package net.cactusthorn.routing;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

public interface Consumer {

    default void init(ServletContext servletContext, ComponentProvider componentProvider) {
    }

    Object consume(Class<?> clazz, MediaType mediaType, RequestData data);
}
