package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import net.cactusthorn.routing.annotation.*;

public class RoutingServletTest {

    public static final Producer TEST_PRODUCER = (object, mediaType, req, resp) -> {
        resp.getWriter().write(String.valueOf("TEST_PRODUCER"));
    };

    @Path("/") //
    public static class EntryPoint1 {

        public Integer m1(@PathParam("in") Integer val) {
            return val;
        }

        @GET //
        public String root() {
            return "ROOT";
        }

        @GET @Path("api/get") //
        public String m0() {
            return "GET";
        }

        @GET @Path("api/null") //
        public String nullResponse() {
            return null;
        }

        @HEAD @Path("api/head") //
        public String head() {
            return "HEAD";
        }

        @POST @Path("api/post") //
        public String post() {
            return "POST";
        }

        @PUT @Path("api/put") @Consumes("application/json") //
        public String put() {
            return "PUT";
        }

        @DELETE @Path("api/delete") //
        public String delete() {
            return "DELETE";
        }

        @OPTIONS @Path("api/options") //
        public String options() {
            return "OPTIONS";
        }

        @TRACE @Path("api/trace") //
        public String trace() {
            return "TRACE";
        }

        @GET @Produces("aa/bb") @Path("api/produce") //
        public void produce() {
            return;
        }

        @GET @Path("api/wrong/{var}") //
        public void wrong(@PathParam("var") Integer var) {
            return;
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new EntryPoint1();
        }
    }

    static RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider()).addEntryPoint(EntryPoint1.class)
            .addProducer("aa/bb", TEST_PRODUCER).build();
    static RoutingServlet servlet = new RoutingServlet(config);

    HttpServletRequest req;
    HttpServletResponse resp;
    StringWriter stringWriter;

    @BeforeEach //
    void setUp() throws IOException {
        req = Mockito.mock(HttpServletRequest.class);
        resp = Mockito.mock(HttpServletResponse.class);
        stringWriter = new StringWriter();
        Mockito.when(resp.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    @Test //
    public void wrong() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/wrong/abc");

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(404, code.getValue());
    }

    @Test //
    public void head() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/head");
        servlet.doHead(req, resp);
        assertEquals("HEAD", stringWriter.toString());
    }

    @Test //
    public void post() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/post");
        servlet.doPost(req, resp);
        assertEquals("POST", stringWriter.toString());
    }

    @Test //
    public void put() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/put");
        Mockito.when(req.getContentType()).thenReturn("application/json");
        servlet.doPut(req, resp);
        assertEquals("PUT", stringWriter.toString());
    }

    @Test //
    public void wrongConsumes() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/put");
        Mockito.when(req.getContentType()).thenReturn("");

        servlet.doPut(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(404, code.getValue());
    }

    @Test //
    public void delete() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/delete");
        servlet.doDelete(req, resp);
        assertEquals("DELETE", stringWriter.toString());
    }

    @Test //
    public void options() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/options");
        servlet.doOptions(req, resp);
        assertEquals("OPTIONS", stringWriter.toString());
    }

    @Test //
    public void trace() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/trace");
        servlet.doTrace(req, resp);
        assertEquals("TRACE", stringWriter.toString());
    }

    @Test //
    public void produce() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/produce");
        servlet.doGet(req, resp);
        assertEquals("TEST_PRODUCER", stringWriter.toString());
    }

    @Test //
    public void simple() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/get");
        servlet.doGet(req, resp);
        assertEquals("GET", stringWriter.toString());
    }

    @Test //
    public void closedUri() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/get/");
        servlet.doGet(req, resp);
        assertEquals("GET", stringWriter.toString());
    }

    @Test //
    public void rootNull() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn(null);
        servlet.doGet(req, resp);
        assertEquals("ROOT", stringWriter.toString());
    }

    @Test //
    public void root() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/");
        servlet.doGet(req, resp);
        assertEquals("ROOT", stringWriter.toString());
    }

    @Test //
    public void rootEmpty() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("");
        servlet.doGet(req, resp);
        assertEquals("ROOT", stringWriter.toString());
    }

    @Test //
    public void notFound() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/aaaabbb");

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(404, code.getValue());
    }

    @Test //
    public void nullResponse() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("api/null");

        servlet.doGet(req, resp);

        assertEquals("", stringWriter.toString());
    }

    @Test //
    public void noEntryPoints() throws ServletException, IOException {
        RoutingConfig c = RoutingConfig.builder(new EntryPoint1Provider()).build();
        RoutingServlet s = new RoutingServlet(c);

        Mockito.when(req.getPathInfo()).thenReturn("/api/get");

        s.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(404, code.getValue());
    }
}
