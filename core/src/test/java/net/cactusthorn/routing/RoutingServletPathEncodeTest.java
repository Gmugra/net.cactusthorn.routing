package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import java.util.Collections;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.GET;

import javax.ws.rs.HttpMethod;

import javax.ws.rs.Path;

import javax.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class RoutingServletPathEncodeTest {

    @Path("/") //
    public static class EntryPoint1 {
        @GET @Path("api/get%C3%BCit") //
        public String testEncode() {
            return "FOUND!";
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {
        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    static RoutingServlet servlet;
    static {
        RoutingServlet tmpServlet = new RoutingServlet(CONFIG);
        RoutingServlet spyServlet = Mockito.spy(tmpServlet);
        Mockito.doReturn(null).when(spyServlet).getServletContext();
        try {
            spyServlet.init();
        } catch (ServletException e) {
            e.printStackTrace();
        }
        servlet = spyServlet;
    }

    HttpServletRequest req;
    HttpServletResponse resp;
    ServletTestOutputStream outputStream;

    @BeforeAll //
    static void setUpLogger() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.FINE);
        // switch off default Handlers to do not get anything in console
        for (Handler h : rootLogger.getHandlers()) {
            h.setLevel(Level.OFF);
        }
    }

    @BeforeEach //
    void setUp() throws IOException {
        req = Mockito.mock(HttpServletRequest.class);
        resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getHeaders(HttpHeaders.ACCEPT)).thenReturn(Collections.emptyEnumeration());
        outputStream = new ServletTestOutputStream();
        Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
    }

    @Test //
    public void encode() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/getüit");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
        servlet.service(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).setStatus(code.capture());
        assertEquals(200, code.getValue());
        assertEquals("FOUND!", outputStream.toString());
    }
}
