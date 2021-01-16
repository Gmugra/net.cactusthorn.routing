package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.ServletTestInputStream;

public class BodyReaderParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {
        @POST @Consumes(MediaType.TEXT_PLAIN) public void context(StringBuilder input) {
        }

        @POST @Consumes(MediaType.TEXT_PLAIN) public void stream(InputStream input) {
        }

        @POST @Consumes(MediaType.TEXT_PLAIN) public void reader(Reader input) {
        }

        @PATCH @Consumes(MediaType.TEXT_PLAIN) public void wrongtype(Runtime input) {
        }

        @POST public void wrongNoConsumes(StringBuilder input) {
        }

        @PUT public void noConsumes(InputStream input) {
        }

        @GET public void get(InputStream input) {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    private static RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addEntryPoint(EntryPoint1.class).build();

    @BeforeAll //
    protected static void beforeAll() throws Exception {
        CONFIG.bodyReaders().forEach(r -> r.init(null, CONFIG));
    }

    @Override @BeforeEach //
    protected void setUp() throws Exception {
        super.setUp();

        Mockito.when(request.getContentType()).thenReturn(MediaType.TEXT_PLAIN);
        Mockito.when(request.getCharacterEncoding()).thenReturn("UTF-8");
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

        ServletInputStream is = new ServletTestInputStream("THIS IS IT");
        Mockito.when(request.getInputStream()).thenReturn(is);
    }

    @Test //
    public void ok() throws Exception {
        Method m = findMethod(EntryPoint1.class, "context");
        Parameter p = m.getParameters()[0];
        MethodParameter body = MethodParameter.Factory.create(m, p, null, CONFIG, mediaTypes(MediaType.TEXT_PLAIN_TYPE));
        StringBuilder result = (StringBuilder) body.findValue(request, null, null, null);
        assertEquals("THIS IS IT", result.toString());
    }

    @Test //
    public void setCharset() throws Exception {
        Method m = findMethod(EntryPoint1.class, "context");
        Parameter p = m.getParameters()[0];
        MethodParameter body = MethodParameter.Factory.create(m, p, null, CONFIG, mediaTypes(MediaType.TEXT_PLAIN_TYPE));
        StringBuilder result = (StringBuilder) body.findValue(request, null, null, null);
        assertEquals("THIS IS IT", result.toString());
    }

    @Test //
    public void stream() throws Exception {
        Method m = findMethod(EntryPoint1.class, "stream");
        Parameter p = m.getParameters()[0];
        MethodParameter body = MethodParameter.Factory.create(m, p, null, CONFIG, mediaTypes(MediaType.TEXT_PLAIN_TYPE));
        InputStream result = (InputStream) body.findValue(request, null, null, null);
        assertNotNull(result);
    }

    @Test //
    public void reader() throws Exception {
        Method m = findMethod(EntryPoint1.class, "reader");
        Parameter p = m.getParameters()[0];
        MethodParameter body = MethodParameter.Factory.create(m, p, null, CONFIG, mediaTypes(MediaType.TEXT_PLAIN_TYPE));
        Reader result = (Reader) body.findValue(request, null, null, null);
        assertNotNull(result);
    }

    @Test //
    public void noConsumes() throws Exception {
        Mockito.when(request.getContentType()).thenReturn(MediaType.WILDCARD);
        Method m = findMethod(EntryPoint1.class, "noConsumes");
        Parameter p = m.getParameters()[0];
        MethodParameter body = MethodParameter.Factory.create(m, p, null, CONFIG, mediaTypes(MediaType.WILDCARD_TYPE));
        InputStream result = (InputStream) body.findValue(request, null, null, null);
        assertNotNull(result);
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void testException(String method, MediaType mediaType) throws Exception {
        Method m = findMethod(EntryPoint1.class, method);
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, null, CONFIG, mediaTypes(mediaType)));
    }

    @Test //
    public void headers() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("aaaaa", "bbbb");
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers.keySet()));
        Mockito.when(request.getHeaders("aaaaa")).thenReturn(Collections.enumeration(headers.values()));

        Method m = findMethod(EntryPoint1.class, "stream");
        Parameter p = m.getParameters()[0];
        MethodParameter.Factory.create(m, p, null, CONFIG, mediaTypes(MediaType.TEXT_PLAIN_TYPE));
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("get", MediaType.WILDCARD),
            Arguments.of("wrongtype", MediaType.TEXT_PLAIN_TYPE));
        // @formatter:on
    }
}
