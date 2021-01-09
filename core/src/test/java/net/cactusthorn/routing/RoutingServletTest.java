package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.producer.Producer;
import net.cactusthorn.routing.validate.ParametersValidationException;
import net.cactusthorn.routing.validate.ParametersValidator;

public class RoutingServletTest {

    public static final Producer TEST_PRODUCER = (object, template, mediaType, req, resp) -> {
        resp.getWriter().write(String.valueOf(object));
    };

    public static final ParametersValidator TEST_VALIDATOR = (object, method, parameters) -> {
        throw new ParametersValidationException("abc");
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
            return Response.builder().setBody("FROM RESPONSE").setContentType("aa/bb").build();
        }

        @GET @Path("api/response/nocontent") //
        public Response responseNoContent() {
            return Response.builder().build();
        }

        @GET @Path("api/response/all") @Produces("aa/bb") //
        public Response responseAll() {
            Cookie cookie = new Cookie("a", "b");
            return Response.builder().skipProducer().setBody("FROM RESPONSE").setCharacterEncoding("KOI8-R").setTemplate("TTT")
                    .setStatus(201).addCookie(cookie).addHeader("h", "v").addIntHeader("hi", 10).addDateHeader("hd", 20L).build();
        }

        @GET @Path("api/redirect") //
        public Response redirect() throws URISyntaxException {
            return Response.builder().seeOther(new URI("/xyz")).build();
        }

        @GET @UserRoles({ "somerole" }) @Path("api/role") //
        public Response role() throws URISyntaxException {
            return Response.builder().seeOther(new URI("/xyz")).build();
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
        assertEquals("DELETE", stringWriter.toString());
    }

    @Test //
    public void options() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/options");
        servlet.doOptions(req, resp);
        assertEquals("OPTIONS", stringWriter.toString());
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
        Mockito.when(resp.getStatus()).thenReturn(HttpServletResponse.SC_OK);

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).setStatus(code.capture());

        assertEquals(204, code.getValue());
    }

    @Test //
    public void responseAll() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/response/all");
        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<String> characterEncoding = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Cookie> cookie = ArgumentCaptor.forClass(Cookie.class);

        ArgumentCaptor<String> headerName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValue = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<String> intHeaderName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> intHeaderValue = ArgumentCaptor.forClass(Integer.class);

        ArgumentCaptor<String> dateHeaderName = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Long> dateHeaderValue = ArgumentCaptor.forClass(Long.class);

        Mockito.verify(resp).setStatus(code.capture());
        Mockito.verify(resp).setCharacterEncoding(characterEncoding.capture());
        Mockito.verify(resp).addCookie(cookie.capture());

        Mockito.verify(resp).addHeader(headerName.capture(), headerValue.capture());
        Mockito.verify(resp).addIntHeader(intHeaderName.capture(), intHeaderValue.capture());
        Mockito.verify(resp).addDateHeader(dateHeaderName.capture(), dateHeaderValue.capture());

        assertEquals(201, code.getValue());
        assertEquals("b", cookie.getValue().getValue());
        assertEquals("KOI8-R", characterEncoding.getValue());

        assertEquals("h", headerName.getValue());
        assertEquals("v", headerValue.getValue());

        assertEquals("hi", intHeaderName.getValue());
        assertEquals(10, intHeaderValue.getValue());

        assertEquals("hd", dateHeaderName.getValue());
        assertEquals(20L, dateHeaderValue.getValue());
    }

    @Test //
    public void redirect() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/api/redirect");

        servlet.doGet(req, resp);

        ArgumentCaptor<String> header = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(resp).setStatus(code.capture());
        Mockito.verify(resp).setHeader(Mockito.eq("Location"), header.capture());

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

    public static final Consumer EXCEPTION_CONSUMER = (clazz, mediaType, data) -> {
        throw new RuntimeException("TEST IT");
    };

    @Test //
    public void init() throws ServletException {
        RoutingConfig c = RoutingConfig.builder(new EntryPoint1Provider()).addEntryPoint(EntryPoint1.class)
                .setParametersValidator(TEST_VALIDATOR).addConsumer(new MediaType("aa","bb"), EXCEPTION_CONSUMER).build();
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
