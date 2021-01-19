package net.cactusthorn.routing.demo.jetty;

import net.cactusthorn.routing.*;
import net.cactusthorn.routing.gson.SimpleGsonBodyReader;
import net.cactusthorn.routing.gson.SimpleGsonBodyWriter;
import net.cactusthorn.routing.thymeleaf.SimpleThymeleafBodyWriter;
import net.cactusthorn.routing.validation.javax.SimpleParametersValidator;
import net.cactusthorn.routing.demo.jetty.dagger.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.util.EnumSet;
import java.util.logging.Level;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String... args) {

        // java.util.loggingg -> SLF4j
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        java.util.logging.Logger.getLogger("").setLevel(Level.FINEST);

        HandlerList handlers = new HandlerList();
        handlers.addHandler(createStaticResourcesServlet());
        handlers.addHandler(createRoutingServlet());

        Server jetty = new Server(8080);
        for (Connector connector : jetty.getConnectors()) {
            for (ConnectionFactory factory : connector.getConnectionFactories()) {
                if (factory instanceof HttpConnectionFactory) {
                    ((HttpConnectionFactory) factory).getHttpConfiguration().setSendServerVersion(false);
                }
            }
        }
        jetty.setStopAtShutdown(true);
        jetty.setHandler(handlers);

        try {
            jetty.start();
            jetty.join();
        } catch (Exception e) {
            LOG.error("", e);
        } finally {
            jetty.destroy();
        }
    }

    private static ServletContextHandler createRoutingServlet() {

        Main main = DaggerMain.create();
        ComponentProvider ComponentProvider = new ComponentProviderWithDagger(main);

        // @formatter:off
        RoutingConfig config =
            RoutingConfig.builder(ComponentProvider)
            .addEntryPoint(main.entryPoints().keySet())
            .addEntryPoint(main.sessionBuilder().build().entryPoints().keySet())
            .addBodyWriter(new SimpleGsonBodyWriter<>())
            .addBodyReader(new SimpleGsonBodyReader<>())
            .addBodyWriter(new SimpleThymeleafBodyWriter("/thymeleaf/"))
            .addConverter(LocalDate.class, new LocalDateConverter())
            .setParametersValidator(new SimpleParametersValidator())
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
        servletContextHandler.addFilter(TestUserFilter.class, "/*", EnumSet.of(DispatcherType.INCLUDE, DispatcherType.REQUEST));

        return servletContextHandler;
    }

    private static ServletContextHandler createStaticResourcesServlet() {

        ServletHolder servletHolder = new ServletHolder("Default Servlet", new DefaultServlet());
        servletHolder.setInitOrder(0);

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servletContextHandler.setContextPath("/static");
        servletContextHandler.addServlet(servletHolder, "*.jpg");
        servletContextHandler.addServlet(servletHolder, "*.png");
        servletContextHandler.setBaseResource(Resource.newClassPathResource("/static/"));

        return servletContextHandler;
    }

    public static class TestUserFilter implements Filter {

        @Override //
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override //
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) request;
            chain.doFilter(new TestUserRequestWrapper(req), response);
        }

        @Override //
        public void destroy() {
        }

        public static class TestUserRequestWrapper extends HttpServletRequestWrapper {

            public TestUserRequestWrapper(HttpServletRequest request) {
                super(request);
            }

            @Override //
            public boolean isUserInRole(String role) {
                return "TestRole".equals(role);
            }

            @Override //
            public Principal getUserPrincipal() {
                return () -> {
                    return "TestUser";
                };
            }

        }
    }

}
