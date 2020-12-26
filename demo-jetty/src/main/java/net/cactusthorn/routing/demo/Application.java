package net.cactusthorn.routing.demo;

import net.cactusthorn.routing.annotation.GET;
import net.cactusthorn.routing.annotation.POST;
import net.cactusthorn.routing.annotation.Path;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.annotation.Template;
import net.cactusthorn.routing.annotation.Consumes;
import net.cactusthorn.routing.annotation.Context;
import net.cactusthorn.routing.annotation.FormParam;
import net.cactusthorn.routing.annotation.Produces;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.gson.SimpleGsonConsumer;
import net.cactusthorn.routing.gson.SimpleGsonProducer;
import net.cactusthorn.routing.thymeleaf.SimpleThymeleafProducer;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;

import java.util.List;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cactusthorn.routing.*;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String... args) {

        // @formatter:off
        RoutingConfig config =
            RoutingConfig.builder(new Provider())
            .addEntryPoint(Component.class)
            .addProducer("application/json", new SimpleGsonProducer())
            .addConsumer("application/json", new SimpleGsonConsumer())
            .addProducer("text/html", new SimpleThymeleafProducer("/thymeleaf/"))
            .build();
        // @formatter:on

        ServletHolder servletHolder = new ServletHolder("Routing Servlet", new RoutingServlet(config));
        servletHolder.setInitOrder(0);

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/");
        servletContextHandler.addServlet(servletHolder, "/*");

        Server jetty = new Server(8080);
        for (Connector connector : jetty.getConnectors()) {
            for (ConnectionFactory factory : connector.getConnectionFactories()) {
                if (factory instanceof HttpConnectionFactory) {
                    ((HttpConnectionFactory) factory).getHttpConfiguration().setSendServerVersion(false);
                }
            }
        }
        jetty.setStopAtShutdown(true);
        jetty.setHandler(servletContextHandler);

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

        @GET @Path("rest/api/test{ var : \\d+ }") //
        public String doit(@PathParam("var") int in, @QueryParam("test") Double q) {
            return in + " \u00DF " + q;
        }

        @GET @Path("rest/api/gson") @Produces("application/json") //
        public DataObject doitGson() {
            return new DataObject("The Name \u00DF", 123);
        }

        @POST @Path("rest/api/gson") @Consumes("application/json") //
        public String getitGson(@Context DataObject data) {
            return data.getName();
        }

        @GET @Path("html") @Produces("text/html") @Template("/index.html") //
        public String getitHtml() {
            return "TEST HTML PAGE";
        }

        @POST @Path("html/form") @Consumes("application/x-www-form-urlencoded") @Produces("text/plain") //
        public String doHtml(@FormParam("fname") String fname, @FormParam("lname") String lname, @FormParam("box") List<Integer> box) {
            return fname + " :: " + lname + " :: " + box;
        }
    }

    public static class Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new Component();
        }
    }
}
