package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.util.logging.Logger;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

import javax.annotation.security.RolesAllowed;
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
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import net.cactusthorn.routing.validate.ParametersValidator;

public class RoutingServletTest {

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
        public Response head() {
            return Response.ok().build();
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
        public Response delete() {
            return Response.accepted().build();
        }

        @OPTIONS @Path("api/options") //
        public void options() {
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

        @GET @RolesAllowed({ "somerole" }) @Path("api/role") //
        public Response role() throws URISyntaxException {
            return Response.status(Status.SEE_OTHER).location(new URI("/xyz")).build();
        }

        @GET @Path("api/wrong/produces") @Produces(MediaType.TEXT_HTML) //
        public Response wrongProduces() {
            return Response.ok("some value").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    static RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

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

    HttpServletRequest req;
    HttpServletResponse resp;
    ServletTestOutputStream outputStream;

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
    public void wrong() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/wrong/abc");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).sendError(code.capture(), Mockito.any());
        assertEquals(HttpServletResponse.SC_NOT_FOUND, code.getValue());
    }

    @Test //
    public void defaultService() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/head");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.HEAD);
        servlet.service(req, resp);
        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).setStatus(code.capture());
        assertEquals(200, code.getValue());
    }

    @Test //
    public void head() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/head");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.HEAD);
        servlet.doHead(req, resp);
        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).setStatus(code.capture());
        assertEquals(200, code.getValue());
    }

    @Test //
    public void post() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/post");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.POST);
        Mockito.when(req.getCharacterEncoding()).thenReturn("UTF-8");
        List<String> accept = new ArrayList<>();
        accept.add("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        Mockito.when(req.getHeaders(HttpHeaders.ACCEPT)).thenReturn(Collections.enumeration(accept));
        servlet.doPost(req, resp);
        assertEquals(HttpMethod.POST, outputStream.toString());
    }

    @Test //
    public void notAccept() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/post");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.POST);
        Mockito.when(req.getHeader(HttpHeaders.ACCEPT)).thenReturn(MediaType.APPLICATION_JSON);

        servlet.doPost(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).sendError(code.capture(), Mockito.any());
        assertEquals(406, code.getValue());
    }

    @Test //
    public void put() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/put");
        Mockito.when(req.getContentType()).thenReturn("application/json");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.PUT);
        servlet.doPut(req, resp);
        assertEquals(HttpMethod.PUT, outputStream.toString());
    }

    @Test //
    public void wrongHeader() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/put");
        Mockito.when(req.getContentType()).thenReturn("application/json; WWWWWW");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.PUT);

        servlet.doPut(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).sendError(code.capture(), Mockito.any());
        assertEquals(400, code.getValue());
    }

    @Test //
    public void wrongConsumes() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/put");
        Mockito.when(req.getContentType()).thenReturn("a/b");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.PUT);

        servlet.doPut(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).sendError(code.capture(), Mockito.any());
        assertEquals(415, code.getValue());
    }

    @Test //
    public void delete() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/delete");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.DELETE);
        servlet.doDelete(req, resp);
        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).setStatus(code.capture());
        assertEquals(202, code.getValue());
    }

    @Test //
    public void options() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/options");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.OPTIONS);
        servlet.doOptions(req, resp);
        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).setStatus(code.capture());
        assertEquals(204, code.getValue());
    }

    @Test //
    public void patch() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/patch");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.PATCH);
        Mockito.when(req.getContentType()).thenReturn("");
        servlet.doPatch(req, resp);
        assertEquals(HttpMethod.PATCH, outputStream.toString());
    }

    @Test //
    public void patchService() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/patch");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.PATCH);
        servlet.service(req, resp);
        assertEquals(HttpMethod.PATCH, outputStream.toString());
    }

    @ParameterizedTest //
    @CsvSource(nullValues = { "$$" }, delimiter = ';', value = { "/api/get;GET", "/api/get/;GET", "$$;ROOT", "'';ROOT", "/;ROOT",
            "/api/produce;TEST_PRODUCER", "/api/response;FROM RESPONSE", "api/null;''" }) //
    public void get(String pathInfo, String expectedResponse) throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn(pathInfo);
        servlet.doGet(req, resp);
        assertEquals(expectedResponse, outputStream.toString());
    }

    @Test //
    public void notFound() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/aaaabbb");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(404, code.getValue());
    }

    @Test //
    public void noEntryPoints() throws ServletException, IOException {
        RoutingConfig c = RoutingConfig.builder(new EntryPoint1Provider()).build();
        RoutingServlet servlet = new RoutingServlet(c);
        RoutingServlet s = Mockito.spy(servlet);
        Mockito.doReturn(null).when(s).getServletContext();
        s.init();

        Mockito.when(req.getPathInfo()).thenReturn("/api/get");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);

        s.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(404, code.getValue());
    }

    @ParameterizedTest @ValueSource(strings = { "/api/nocontent", "/api/response/nocontent" }) //
    public void nocontent(String pathInfo) throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn(pathInfo);
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).setStatus(code.capture());

        assertEquals(204, code.getValue());
    }

    @Test //
    public void redirect() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/redirect");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);

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
        RoutingConfig c = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class)
                .setParametersValidator(TEST_VALIDATOR).build();
        RoutingServlet servlet = new RoutingServlet(c);
        RoutingServlet s = Mockito.spy(servlet);
        Mockito.doReturn(null).when(s).getServletContext();
        s.init();

        Mockito.when(req.getPathInfo()).thenReturn("/api/get");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);

        s.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(400, code.getValue());
    }

    @Test //
    public void userRoles() throws ServletException, IOException {
        RoutingConfig c = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class)
                .setParametersValidator(TEST_VALIDATOR).build();
        RoutingServlet servlet = new RoutingServlet(c);
        RoutingServlet s = Mockito.spy(servlet);
        Mockito.doReturn(null).when(s).getServletContext();
        s.init();

        Mockito.when(req.getPathInfo()).thenReturn("/api/role");
        Mockito.when(req.isUserInRole(Mockito.any())).thenReturn(false);
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);

        s.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(403, code.getValue());
    }

    @Test public void wrongProduces() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/wrong/produces");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(req.getHeader(HttpHeaders.ACCEPT)).thenReturn(MediaType.TEXT_HTML);

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).sendError(code.capture(), Mockito.any());

        assertEquals(406, code.getValue());
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
    public void initializationException() throws ServletException {
        RoutingConfig config = RoutingConfig.builder(new EntryPointWrongProvider()).addResource(EntryPointWrong.class).build();
        RoutingServlet servlet = new RoutingServlet(config);
        RoutingServlet s = Mockito.spy(servlet);
        Mockito.doReturn(null).when(s).getServletContext();
        assertThrows(RoutingInitializationException.class, () -> s.init());
    }
}
