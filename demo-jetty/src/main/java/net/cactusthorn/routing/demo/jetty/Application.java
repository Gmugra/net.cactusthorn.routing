package net.cactusthorn.routing.demo.jetty;

import net.cactusthorn.routing.*;
import net.cactusthorn.routing.annotation.GET;
import net.cactusthorn.routing.annotation.HeaderParam;
import net.cactusthorn.routing.annotation.POST;
import net.cactusthorn.routing.annotation.Path;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.annotation.Template;
import net.cactusthorn.routing.annotation.Consumes;
import net.cactusthorn.routing.annotation.Context;
import net.cactusthorn.routing.annotation.CookieParam;
import net.cactusthorn.routing.annotation.DefaultValue;
import net.cactusthorn.routing.annotation.FormParam;
import net.cactusthorn.routing.annotation.FormPart;
import net.cactusthorn.routing.annotation.Produces;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.gson.SimpleGsonConsumer;
import net.cactusthorn.routing.gson.SimpleGsonProducer;
import net.cactusthorn.routing.thymeleaf.SimpleThymeleafProducer;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConnectionFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Cookie;
import javax.servlet.http.Part;

import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    // @Path("/") //
    public static class Component {

        @GET //
        public String doroot() {
            return "ROOT";
        }

        @GET @Path("/nocontent") //
        public void nocontent() {
        }

        @GET @Path("/rest/api/test{ var : \\d+ }") //
        public String doit(@PathParam("var") int in, @DefaultValue("10.5") @QueryParam("test") Double q) {
            return in + " \u00DF " + q;
        }

        @GET @Path("/rest/api/{var : [abc]*}") //
        public String empty(@PathParam("var") @DefaultValue("DEFAULT") String sss) {
            return "|" + sss + "|";
        }

        @GET @Path("rest/api/gson") @Produces("application/json") //
        public DataObject doitGson() {
            return new DataObject("The Name \u00DF", 123);
        }

        @POST @Path("rest/api/gson") @Consumes("application/json") //
        public String getitGson(@Context DataObject data, @HeaderParam("Accept-Encoding") String acceptEncoding) {
            return data.getName() + "; header : " + acceptEncoding;
        }

        @GET @Path("html") @Produces("text/html") @Template("/index.html") //
        public String getitHtml() {
            return "TEST HTML PAGE";
        }

        @GET @Path("html/upload") //
        public Response showUpload() {
            return Response.builder().setContentType("text/html").setTemplate("/fileupload.html").build();
        }

        @POST @Path("html/doupload") @Consumes("multipart/form-data") //
        public String upload(@FormParam("fname") String fname, @FormPart("myfile") Part part, @FormPart("myfile2") Part part2)
                throws IOException {

            String result = fname + " :: ";

            java.nio.file.Path tmpDir = Files.createTempDirectory("");
            String fileName = part.getSubmittedFileName();
            if (!"".equals(fileName)) {
                java.nio.file.Path path = tmpDir.resolve(fileName);
                Files.copy(part.getInputStream(), path);
                result += path + " :: ";
            }

            String fileName2 = part2.getSubmittedFileName();
            if (!"".equals(fileName)) {
                java.nio.file.Path path = tmpDir.resolve(fileName2);
                Files.copy(part2.getInputStream(), path);
                result += path;
            }

            return result;
        }

        // @formatter:off
        @POST @Path("html/form") @Consumes("application/x-www-form-urlencoded") //
        public String doHtml(
                @FormParam("fname") String fname,
                @FormParam("lname") String lname, 
                @FormParam("box") List<Integer> box,
                @CookieParam("JSESSIONID") Cookie jsession) {
            return fname + " :: " + lname + " :: " + box + " :: " + jsession.getValue();
        }
        // @formatter:on
    }

    public static class Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new Component();
        }
    }
}
