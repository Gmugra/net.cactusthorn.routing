package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class RoutingServletProducesTest {

    public static final class EntryPoint1 {

        @GET @Path("testIt") @Produces(MediaType.APPLICATION_XML) public String xml() {
            return "XML";
        }

        @GET @Path("testIt") @Produces(MediaType.APPLICATION_JSON) public String json() {
            return "JSON";
        }

        @GET @Path("testIt") @Produces({ MediaType.APPLICATION_OCTET_STREAM, MediaType.TEXT_HTML }) public String html() {
            return "HTML";
        }
    }

    public static final class EntryPoint1Provider implements ComponentProvider {
        @Override public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    static RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    HttpServletRequest req;
    HttpServletResponse resp;
    ServletTestOutputStream outputStream;

    static RoutingServlet servlet;
    static {
        RoutingServlet tmpServlet = new RoutingServlet(config);
        RoutingServlet spyServlet = Mockito.spy(tmpServlet);
        Mockito.doReturn(null).when(spyServlet).getServletContext();
        try {
            spyServlet.init();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        servlet = spyServlet;
    }

    @BeforeEach void setUp() throws IOException {
        req = Mockito.mock(HttpServletRequest.class);
        resp = Mockito.mock(HttpServletResponse.class);
        outputStream = new ServletTestOutputStream();
        Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
    }

    @Test public void json() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/testIt");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(req.getHeader(HttpHeaders.ACCEPT)).thenReturn(MediaType.APPLICATION_JSON);
        servlet.service(req, resp);
        assertEquals("JSON", outputStream.toString());
    }

    @Test public void xml() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/testIt");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(req.getHeader(HttpHeaders.ACCEPT)).thenReturn(MediaType.APPLICATION_XML);
        servlet.service(req, resp);
        assertEquals("XML", outputStream.toString());
    }

    @Test public void html() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/testIt");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(req.getHeader(HttpHeaders.ACCEPT)).thenReturn(MediaType.TEXT_HTML);
        servlet.service(req, resp);
        assertEquals("HTML", outputStream.toString());
    }
}
