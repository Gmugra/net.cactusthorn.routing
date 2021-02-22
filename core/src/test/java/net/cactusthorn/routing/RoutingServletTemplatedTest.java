package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import net.cactusthorn.routing.annotation.Template;
import net.cactusthorn.routing.body.writer.Templated;
import net.cactusthorn.routing.body.writer.TemplatedMessageBodyWriter;

public class RoutingServletTemplatedTest {

    @Produces(MediaType.TEXT_HTML) public static class TestTemplated implements TemplatedMessageBodyWriter {

        @Override public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return true;
        }

        @Override public void writeTo(Templated templated, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(entityStream, charset))) {
                writer.write(templated.entity().toString());
            }
        }
    }

    @Path("/") //
    public static class EntryPoint1 {

        @POST @Path("api/template") //
        public Response template() {
            Templated t = new Templated("t", "templated result");
            return Response.ok(t).type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8")).build();
        }

        @POST @Path("api/template/A") @Template("/xyz.html") @Produces("text/html,*/*") //
        public String templateA() {
            return "some value";
        }

        @POST @Path("api/template/B") @Produces("text/html,*/*") //
        public Response templateB(@Context HttpServletRequest request, @Context HttpServletResponse response) {
            Templated t = new Templated("t", "templated result", request, response);
            return Response.ok(t).type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8")).build();
        }

        @POST @Path("api/template/C") @Produces("text/html,*/*") //
        public Response templateC(@Context HttpServletRequest request) {
            Templated t = new Templated("t", "templated result", request, null);
            return Response.ok(t).type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8")).build();
        }

        @POST @Path("api/template/D") @Produces("text/html,*/*") //
        public Response templateD(@Context HttpServletResponse response) {
            Templated t = new Templated("t", "templated result", null, response);
            return Response.ok(t).type(MediaType.TEXT_HTML_TYPE.withCharset("UTF-8")).build();
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {
        @Override public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    HttpServletRequest req;
    HttpServletResponse resp;
    ServletTestOutputStream outputStream;

    @BeforeEach //
    void setUp() throws IOException {
        req = Mockito.mock(HttpServletRequest.class);
        resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getHeaders(HttpHeaders.ACCEPT)).thenReturn(Collections.emptyEnumeration());
        outputStream = new ServletTestOutputStream();
        Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
    }

    @Test //
    public void templated() throws ServletException, IOException {
        service("/api/template");
        assertEquals("templated result", outputStream.toString());
    }

    @Test //
    public void templatedB() throws ServletException, IOException {
        service("/api/template/B");
        assertEquals("templated result", outputStream.toString());
    }

    @Test //
    public void templatedC() throws ServletException, IOException {
        service("/api/template/C");
        assertEquals("templated result", outputStream.toString());
    }

    @Test //
    public void templatedD() throws ServletException, IOException {
        service("/api/template/D");
        assertEquals("templated result", outputStream.toString());
    }

    @Test //
    public void templatedA() throws ServletException, IOException {
        service("/api/template/A");
        ArgumentCaptor<String> contentType = ArgumentCaptor.forClass(String.class);
        Mockito.verify(resp).setContentType(contentType.capture());
        assertEquals("text/html", contentType.getValue());
        assertEquals("some value", outputStream.toString());
    }

    private void service(String path) throws ServletException, IOException {
        RoutingConfig c = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).addBodyWriter(new TestTemplated())
                .build();
        RoutingServlet toSpy = new RoutingServlet(c);
        RoutingServlet s = Mockito.spy(toSpy);
        Mockito.doReturn(null).when(s).getServletContext();
        s.init();
        Mockito.when(req.getPathInfo()).thenReturn(path);
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.POST);
        s.service(req, resp);
    }
}
