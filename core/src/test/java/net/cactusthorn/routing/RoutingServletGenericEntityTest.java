package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

public class RoutingServletGenericEntityTest {

    @Path("/") //
    public static final class EntryPoint1 {
        @GET @Path("genericEntity") public Response genericEntity() throws Exception {
            List<String> list = new ArrayList<String>();
            list.add("AAAAAA");
            list.add("BBBBBB");
            GenericEntity<List<String>> entity = new GenericEntity<List<String>>(list) {};
            return Response.ok(entity).build();
        }
    }

    public static final class EntryPoint1Provider implements ComponentProvider {
        @Override public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    public static final class TestWriter implements MessageBodyWriter<List<String>> {

        @Override public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            if (genericType == null ) {
                return false;
            }
            if (!List.class.isAssignableFrom(type)) {
                return false;
            }
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                Type[] genericTypes = parameterizedType.getActualTypeArguments();
                if (genericTypes[0] == String.class) {
                    return true;
                }
            }
            return false;
        }

        @Override public void writeTo(List<String> t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
            try (Writer writer = new BufferedWriter(new OutputStreamWriter(entityStream, charset))) {
                writer.write("TestWriter -> ");
                for (String s : t) {
                    writer.write(s);
                    writer.write(" :: ");
                }
            }
        }
    }
    
    static RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class)
            .addBodyWriter(new TestWriter()).build();

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

    @BeforeEach //
    void setUp() throws IOException {
        req = Mockito.mock(HttpServletRequest.class);
        resp = Mockito.mock(HttpServletResponse.class);
        Mockito.when(req.getHeaders(HttpHeaders.ACCEPT)).thenReturn(Collections.emptyEnumeration());
        outputStream = new ServletTestOutputStream();
        Mockito.when(resp.getOutputStream()).thenReturn(outputStream);
    }

    @Test //
    public void genericEntity() throws ServletException, IOException {
        Mockito.when(req.getPathInfo()).thenReturn("/genericEntity");
        Mockito.when(req.getMethod()).thenReturn(HttpMethod.GET);
        Mockito.when(req.getCharacterEncoding()).thenReturn("UTF-8");

        servlet.doGet(req, resp);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);
        Mockito.verify(resp).setStatus(code.capture());
        assertEquals(200, code.getValue());

        assertEquals("TestWriter -> AAAAAA :: BBBBBB :: ", outputStream.toString());
    }
}
