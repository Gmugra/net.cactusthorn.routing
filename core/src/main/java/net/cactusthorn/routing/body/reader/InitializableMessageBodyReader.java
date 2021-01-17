package net.cactusthorn.routing.body.reader;

import javax.servlet.ServletContext;
import javax.ws.rs.ext.MessageBodyReader;

import net.cactusthorn.routing.RoutingConfig;

public interface InitializableMessageBodyReader<T> extends MessageBodyReader<T> {

    default void init(ServletContext servletContext, RoutingConfig routingConfig) {
    }
}
