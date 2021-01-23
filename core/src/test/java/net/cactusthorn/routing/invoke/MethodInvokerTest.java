package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.ServerErrorException;
import javax.ws.rs.core.Context;
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
import net.cactusthorn.routing.ServletTestInputStream;
import net.cactusthorn.routing.invoke.MethodInvoker.ReturnObjectInfo;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.validate.ParametersValidator;

public class MethodInvokerTest extends InvokeTestAncestor {

    private static final ParametersValidator VALIDATOR = (object, method, parameters) -> {
    };

    public static class EntryPoint1 {

        public Integer m1(@PathParam("in") Integer val) {
            return val;
        }

        public String m0() {
            return "OK";
        }

        public String m3(@Context HttpServletRequest request, @PathParam("in") Integer val) {
            return (String) request.getAttribute("req") + val;
        }

        public String m4(@QueryParam("in") Double val) {
            return "" + val;
        }

        public String m5(@Context ServletContext context) {
            return (String) context.getAttribute("test");
        }

        public String m6(@Context HttpServletResponse response) {
            return response.getCharacterEncoding();
        }

        @POST @Consumes(MediaType.TEXT_PLAIN) public String m7(StringBuffer buf) {
            return buf.toString();
        }

        public void m8(@Context HttpServletResponse response) throws Exception {
            throw new Exception("test exception");
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    HttpServletResponse response;

    ServletContext context;

    @BeforeAll
    public static void loggingOff() {
        java.util.logging.Logger.getLogger("").setLevel(Level.OFF);
    }

    @BeforeEach @Override //
    protected void setUp() throws Exception {
        super.setUp();
        context = Mockito.mock(ServletContext.class);
        response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(request.getAttribute("req")).thenReturn("EVE");
        Mockito.when(request.getParameter("in")).thenReturn("120.5");
        Mockito.when(context.getAttribute("test")).thenReturn("CONTEXT");
        Mockito.when(response.getCharacterEncoding()).thenReturn("KOI8-R");
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void invokeMethod(String methodName, PathValues pathValues, Object expectedResult) {

        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class)
                .setParametersValidator(VALIDATOR).build();

        Method method = findMethod(EntryPoint1.class, methodName);
        MethodInvoker caller = new MethodInvoker(config, EntryPoint1.class, method, DEFAULT_CONTENT_TYPES);

        Object result = caller.invoke(request, response, context, pathValues);

        assertEquals(expectedResult, result);
    }

    @Test //
    public void invokeM7() throws IOException {

        Mockito.when(request.getInputStream()).thenReturn(new ServletTestInputStream("TO HAVE BODY"));
        Mockito.when(request.getContentType()).thenReturn(MediaType.TEXT_PLAIN);
        Mockito.when(request.getCharacterEncoding()).thenReturn("UTF-8");
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());

        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class)
                .setParametersValidator(VALIDATOR).build();
        config.bodyReaders().forEach(r -> r.init(null, config));

        Set<MediaType> consumesMediaTypes = new HashSet<>();
        consumesMediaTypes.add(MediaType.TEXT_PLAIN_TYPE);

        Method method = findMethod(EntryPoint1.class, "m7");

        MethodInvoker caller = new MethodInvoker(config, EntryPoint1.class, method, consumesMediaTypes);

        String result = (String) caller.invoke(request, response, null, null);

        assertEquals("TO HAVE BODY", result);
    }
    
    @Test //
    public void invokeM8() throws IOException {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class)
                .setParametersValidator(VALIDATOR).build();

        Method method = findMethod(EntryPoint1.class, "m8");
        MethodInvoker caller = new MethodInvoker(config, EntryPoint1.class, method, DEFAULT_CONTENT_TYPES);

        ReturnObjectInfo returnObjectInfo = caller.returnObjectInfo();
        assertEquals(Void.TYPE, returnObjectInfo.type());
        assertEquals(Void.TYPE, returnObjectInfo.genericType());
        assertEquals(0, returnObjectInfo.annotations().length);

        assertThrows(ServerErrorException.class, () -> caller.invoke(request, response, context, null));
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("m0", PathValues.EMPTY, "OK"),
            Arguments.of("m1", new PathValues("in", "123"), 123),
            Arguments.of("m3", new PathValues("in", "123"), "EVE123"),
            Arguments.of("m4", null, "120.5"),
            Arguments.of("m5", null, "CONTEXT"),
            Arguments.of("m6", null, "KOI8-R"));
        // @formatter:on
    }
}
