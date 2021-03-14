package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

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
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class RoutingServletExceptionMapperTest {

    @Path("/") public static final class EntryPoint1 {
        @GET @Path("exceptionMapper") public Response exceptionMapper() throws Exception {
            throw new UnsupportedOperationException();
        }

        @GET @Path("unknown") public Response unknown() throws Exception {
            throw new IllegalAccessError("This is error");
        }
    }

    public static final class EntryPoint1Provider implements ComponentProvider {
        @Override public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    public static class TestExceptionMapper implements Cloneable, ExceptionMapper<UnsupportedOperationException> {
        @Override public Response toResponse(UnsupportedOperationException exception) {
            return Response.status(Response.Status.CONFLICT).build();
        }
    }

    static RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class)
            .addExceptionMapper(new TestExceptionMapper()).build();

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

    @BeforeAll //
    static void setUpLogger() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.FINE);
        //switch off default Handlers to do not get anything in console
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
    public void exceptionMapper() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/exceptionMapper");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(req.getCharacterEncoding()).thenReturn("UTF-8");

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).setStatus(code.capture());
        assertEquals(Response.Status.CONFLICT.getStatusCode(), code.getValue());
    }

    @Test //
    public void unknown() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/unknown");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(req.getCharacterEncoding()).thenReturn("UTF-8");

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).sendError(code.capture());
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), code.getValue());
    }
}
