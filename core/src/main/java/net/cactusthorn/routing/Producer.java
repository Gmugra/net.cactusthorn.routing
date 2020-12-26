package net.cactusthorn.routing;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Producer {

    default void init(ServletContext servletContext, ComponentProvider componentProvider) {
    }

    void produce(Object object, String template, String mediaType, HttpServletRequest req, HttpServletResponse resp) throws IOException;
}
