package net.cactusthorn.routing.body;

import javax.servlet.ServletContext;

import net.cactusthorn.routing.RoutingConfig;

public interface Initializable {

    void init(ServletContext servletContext, RoutingConfig routingConfig);
}
