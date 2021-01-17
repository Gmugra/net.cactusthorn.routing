package net.cactusthorn.routing.body.writer;

import javax.servlet.ServletContext;
import javax.ws.rs.ext.MessageBodyWriter;

import net.cactusthorn.routing.RoutingConfig;

public interface InitializableMessageBodyWriter<T> extends MessageBodyWriter<T> {

    default void init(ServletContext servletContext, RoutingConfig routingConfig) {
    }
}
