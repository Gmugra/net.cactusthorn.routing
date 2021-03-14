package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
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
import net.cactusthorn.routing.util.ProvidersImpl;

public class BodyReaderParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {
        @POST @Consumes(MediaType.TEXT_PLAIN) public void context(StringBuilder input) {
        }

        @POST @Consumes(MediaType.TEXT_PLAIN) public void stream(InputStream input) {
        }

        @POST public void string(String input) {
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

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    @BeforeAll //
    protected static void beforeAll() throws Exception {
        ((ProvidersImpl)CONFIG.providers()).init(null, CONFIG);
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
        MethodParameter body = parameterInfo(EntryPoint1.class, "context", CONFIG);
        StringBuilder result = (StringBuilder) body.findValue(request, null, null, null);
        assertEquals("THIS IS IT", result.toString());
    }

    @Test //
    public void setCharset() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("aaaaa", "bbbb");
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers.keySet()));
        Mockito.when(request.getHeaders("aaaaa")).thenReturn(Collections.enumeration(headers.values()));

        MethodParameter body = parameterInfo(EntryPoint1.class, "context", CONFIG);
        StringBuilder result = (StringBuilder) body.findValue(request, null, null, null);
        assertEquals("THIS IS IT", result.toString());
    }

    @Test //
    public void stream() throws Exception {
        MethodParameter body = parameterInfo(EntryPoint1.class, "stream", CONFIG);
        InputStream result = (InputStream) body.findValue(request, null, null, null);
        assertNotNull(result);
    }

    @Test //
    public void string() throws Exception {
        MethodParameter body = parameterInfo(EntryPoint1.class, "string", CONFIG);
        String result = (String) body.findValue(request, null, null, null);
        assertEquals("THIS IS IT", result);
    }

    @Test //
    public void noConsumes() throws Exception {
        Mockito.when(request.getContentType()).thenReturn(MediaType.WILDCARD);
        MethodParameter body = parameterInfo(EntryPoint1.class, "noConsumes", CONFIG);
        InputStream result = (InputStream) body.findValue(request, null, null, null);
        assertNotNull(result);
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void testException(String method) throws Exception {
        assertThrows(RoutingInitializationException.class, () -> parameterInfo(EntryPoint1.class, method, CONFIG));
    }

    @Test //
    public void headers() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("aaaaa", "bbbb");
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.enumeration(headers.keySet()));
        Mockito.when(request.getHeaders("aaaaa")).thenReturn(Collections.enumeration(headers.values()));

        parameterInfo(EntryPoint1.class, "stream", CONFIG);
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("get"),
            Arguments.of("wrongtype"));
        // @formatter:on
    }
}
