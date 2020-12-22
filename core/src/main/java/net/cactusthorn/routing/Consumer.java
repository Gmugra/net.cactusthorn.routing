package net.cactusthorn.routing;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

public interface Consumer {

    default void init(ServletContext servletContext, ComponentProvider componentProvider) {
    }

    Object produce(Class<?> clazz, String mediaType, HttpServletRequest req) throws IOException;
}
