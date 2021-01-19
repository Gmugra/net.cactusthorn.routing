package net.cactusthorn.routing.body.reader;

import javax.servlet.ServletContext;
import javax.ws.rs.ext.MessageBodyReader;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.body.Initializable;

public interface InitializableMessageBodyReader<T> extends Initializable, MessageBodyReader<T> {

    @Override
    default void init(ServletContext servletContext, RoutingConfig routingConfig) {
    }
}
