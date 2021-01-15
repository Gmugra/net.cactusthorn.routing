package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.producer.Producer;
import net.cactusthorn.routing.validate.ParametersValidator;

public class RoutingServletTest {

    public static final Producer TEST_PRODUCER = (object, template, mediaType, req, resp) -> {
        resp.getWriter().write(String.valueOf(object));
    };

    public static final ParametersValidator TEST_VALIDATOR = (object, method, parameters) -> {
        throw new BadRequestException("abc");
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

        @PATCH @Path("api/patch") //
        public String patch() {
            return "PATCH";
        }

        @GET @Produces("aa/bb") @Path("api/produce") //
        public String produce() {
            return "TEST_PRODUCER";
        }

        @GET @Path("api/wrong/{var}") //
        public void wrong(@PathParam("var") Integer var) {
            return;
        }

        @GET @Path("api/nocontent") //
        public void nocontent() {
        }

        @GET @Path("api/response") //
        public Response response() {
            return Response.ok("FROM RESPONSE").type("aa/bb").build();
        }

        @GET @Path("api/response/nocontent") //
        public Response responseNoContent() {
            return Response.status(Status.NO_CONTENT).build();
        }

        @GET @Path("api/redirect") //
        public Response redirect() throws URISyntaxException {
            return Response.status(Status.SEE_OTHER).location(new URI("/xyz")).build();
        }

        @GET @UserRoles({ "somerole" }) @Path("api/role") //
        public Response role() throws URISyntaxException {
            return Response.status(Status.SEE_OTHER).location(new URI("/xyz")).build();
        }

        @GET @Path("api/template") //
        public Response template(@Context HttpServletRequest request, @Context HttpServletResponse response) {
            Templated t = new Templated(request, response, "t", "templated result");
            return Response.ok(t).type("aa/bb").build();
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
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

        assertEquals(HttpServletResponse.SC_BAD_REQUEST, code.getValue());
    }

    @Test //
    public void templated() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/template");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);
        servlet.service(req, resp);
        assertEquals("templated result", stringWriter.toString());
    }

    @Test //
    public void defaultService() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/head");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.HEAD);
        servlet.service(req, resp);
        assertEquals(HttpMethod.HEAD, stringWriter.toString());
    }

    @Test //
    public void head() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/head");
        servlet.doHead(req, resp);
        assertEquals(HttpMethod.HEAD, stringWriter.toString());
    }

    @Test //
    public void post() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/post");
        servlet.doPost(req, resp);
        assertEquals(HttpMethod.POST, stringWriter.toString());
    }

    @Test //
    public void put() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/put");
        Mockito.when(req.getContentType()).thenReturn("application/json");
        servlet.doPut(req, resp);
        assertEquals(HttpMethod.PUT, stringWriter.toString());
    }

    @Test //
    public void wrongHeader() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/put");
        Mockito.when(req.getContentType()).thenReturn("application/json; WWWWWW");

        servlet.doPut(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(400, code.getValue());
    }

    @Test //
    public void wrongConsumes() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/put");
        Mockito.when(req.getContentType()).thenReturn("a/b");

        servlet.doPut(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(415, code.getValue());
    }

    @Test //
    public void delete() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/delete");
        servlet.doDelete(req, resp);
        assertEquals(HttpMethod.DELETE, stringWriter.toString());
    }

    @Test //
    public void options() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/options");
        servlet.doOptions(req, resp);
        assertEquals(HttpMethod.OPTIONS, stringWriter.toString());
    }

    @Test //
    public void patch() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/patch");
        servlet.doPatch(req, resp);
        assertEquals(HttpMethod.PATCH, stringWriter.toString());
    }

    @Test //
    public void patchService() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/patch");
        Mockito.when(req.getMethod()).thenReturn("PATCH");
        servlet.service(req, resp);
        assertEquals(HttpMethod.PATCH, stringWriter.toString());
    }

    @ParameterizedTest //
    @CsvSource(nullValues = { "$$" }, delimiter = ';', value = { "/api/get;GET", "/api/get/;GET", "$$;ROOT", "'';ROOT", "/;ROOT",
            "/api/produce;TEST_PRODUCER", "/api/response;FROM RESPONSE", "api/null;''" }) //
    public void get(String pathInfo, String expectedResponse) throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn(pathInfo);
        servlet.doGet(req, resp);
        assertEquals(expectedResponse, stringWriter.toString());
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
    public void noEntryPoints() throws ServletException, IOException {
        RoutingConfig c = RoutingConfig.builder(new EntryPoint1Provider()).build();
        RoutingServlet s = new RoutingServlet(c);

        Mockito.when(req.getPathInfo()).thenReturn("/api/get");

        s.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(404, code.getValue());
    }

    @ParameterizedTest @ValueSource(strings = { "/api/nocontent", "/api/response/nocontent" }) //
    public void nocontent(String pathInfo) throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn(pathInfo);

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).setStatus(code.capture());

        assertEquals(204, code.getValue());
    }

    @Test //
    public void redirect() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/redirect");

        servlet.doGet(req, resp);

        ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).setStatus(code.capture());
        Mockito.verify(resp).addHeader(Mockito.eq("Location"), header.capture());

        assertEquals("/xyz", header.getValue());
        assertEquals(303, code.getValue());
    }

    @Test //
    public void validation() throws ServletException, IOException {
        RoutingConfig c = RoutingConfig.builder(new EntryPoint1Provider()).addEntryPoint(EntryPoint1.class)
                .setParametersValidator(TEST_VALIDATOR).build();
        RoutingServlet s = new RoutingServlet(c);

        Mockito.when(req.getPathInfo()).thenReturn("/api/get");

        s.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(400, code.getValue());
    }

    @Test //
    public void userRoles() throws ServletException, IOException {
        RoutingConfig c = RoutingConfig.builder(new EntryPoint1Provider()).addEntryPoint(EntryPoint1.class)
                .setParametersValidator(TEST_VALIDATOR).build();
        RoutingServlet s = new RoutingServlet(c);

        Mockito.when(req.getPathInfo()).thenReturn("/api/role");
        Mockito.when(req.isUserInRole(Mockito.any())).thenReturn(false);

        s.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(403, code.getValue());
    }

    @Test //
    public void init() throws ServletException {
        RoutingConfig c = RoutingConfig.builder(new EntryPoint1Provider()).addEntryPoint(EntryPoint1.class)
                .setParametersValidator(TEST_VALIDATOR).build();
        RoutingServlet servlet = new RoutingServlet(c);
        RoutingServlet spyServlet = Mockito.spy(servlet);
        Mockito.doReturn(null).when(spyServlet).getServletContext();
        spyServlet.init();
    }

    public static class EntryPointWrong {

        @GET @Path("/dddd{/") //
        public void m4() {
        }
    }

    public static class EntryPointWrongProvider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPointWrong();
        }
    }

    @Test //
    public void initializationException() {
        RoutingConfig config = RoutingConfig.builder(new EntryPointWrongProvider()).addEntryPoint(EntryPointWrong.class).build();
        assertThrows(RoutingInitializationException.class, () -> new RoutingServlet(config));
    }
}
