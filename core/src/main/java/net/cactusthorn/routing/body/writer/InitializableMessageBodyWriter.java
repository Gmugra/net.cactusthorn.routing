package net.cactusthorn.routing.body.writer;

import javax.servlet.ServletContext;
import javax.ws.rs.ext.MessageBodyWriter;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.body.Initializable;

public interface InitializableMessageBodyWriter<T> extends Initializable, MessageBodyWriter<T> {

    @Override
    default void init(ServletContext servletContext, RoutingConfig routingConfig) {
    }
}
