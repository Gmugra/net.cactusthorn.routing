package net.cactusthorn.routing.demo.jetty;

import net.cactusthorn.routing.*;
import net.cactusthorn.routing.gson.SimpleGsonConsumer;
import net.cactusthorn.routing.gson.SimpleGsonProducer;
import net.cactusthorn.routing.thymeleaf.SimpleThymeleafProducer;

import net.cactusthorn.routing.demo.jetty.dagger.*;

import java.time.LocalDate;

import javax.servlet.MultipartConfigElement;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String... args) {

        Main main = DaggerMain.create();
        ComponentProvider ComponentProvider = new ComponentProviderWithDagger(main);

        // @formatter:off
        RoutingConfig config =
            RoutingConfig.builder(ComponentProvider)
            .addEntryPoint(main.entryPoints().keySet())
            .addEntryPoint(main.sessionBuilder().build().entryPoints().keySet())
            .addProducer("application/json", new SimpleGsonProducer())
            .addConsumer("application/json", new SimpleGsonConsumer())
            .addProducer("text/html", new SimpleThymeleafProducer("/thymeleaf/"))
            .addConverter(LocalDate.class, new LocalDateConverter())
            .build();
        // @formatter:on

        // location="/tmp", fileSizeThreshold=1024*1024, maxFileSize=1024*1024*5,
        // maxRequestSize=1024*1024*5*5
        MultipartConfigElement multipartConfig = new MultipartConfigElement("/tmp", 1024 * 1024, 1024 * 1024 * 5, 1024 * 1024 * 5 * 5);

        ServletHolder servletHolder = new ServletHolder("Routing Servlet", new RoutingServlet(config));
        servletHolder.setInitOrder(0);
        servletHolder.getRegistration().setMultipartConfig(multipartConfig);

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
}
