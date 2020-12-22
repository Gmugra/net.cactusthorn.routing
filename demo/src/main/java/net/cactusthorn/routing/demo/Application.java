package net.cactusthorn.routing.demo;

import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.gson.SimpleGsonProducer;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cactusthorn.routing.*;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String... args) {

        RoutingConfig config = RoutingConfig.builder(new Provider()).addEntryPoint(Component.class)
                .addProducer("application/json", new SimpleGsonProducer(true)).build();

        ServletHolder servletHolder = new ServletHolder(new RoutingServlet(config));
        servletHolder.setInitOrder(0);

        ServletContextHandler servletContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContext.setContextPath("/");
        servletContext.addServlet(servletHolder, "/rest/*");

        Server jetty = new Server(8080);
        jetty.setStopAtShutdown(true);
        jetty.setHandler(servletContext);

        try {
            jetty.start();
            jetty.join();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            jetty.destroy();
        }
    }

    @Path("/") //
    public static class Component {

        @GET //
        public String doroot() {
            return "ROOT";
        }

        @GET @Path("api/test{ var : \\d+ }") //
        public String doit(@PathParam("var") int in, @QueryParam("test") Double q) {
            return in + " \u00DF " + q;
        }

        @GET @Path("api/test/gson") @Produces("application/json") //
        public DataObject doitGson() {
            return new DataObject("The Name \u00DF", 123);
        }
    }

    public static class Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new Component();
        }
    }
}
