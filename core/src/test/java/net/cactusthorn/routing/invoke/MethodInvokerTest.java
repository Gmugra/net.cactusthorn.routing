package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.*;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.Consumer;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.validate.ParametersValidationException;
import net.cactusthorn.routing.validate.ParametersValidator;

public class MethodInvokerTest extends InvokeTestAncestor {

    public static final Consumer TEST_CONSUMER = (clazz, mediaType, data) -> {
        return new java.util.Date();
    };

    private static final ParametersValidator VALIDATOR = (object, method, parameters) -> {
    };

    public static class EntryPoint1 {

        public Integer m1(@PathParam("in") Integer val) {
            return val;
        }

        public String m0() {
            return "OK";
        }

        public String m2(HttpSession session) {
            return (String) session.getAttribute("test");
        }

        public String m3(HttpServletRequest request, @PathParam("in") Integer val, String willBeNull) {
            return (String) request.getAttribute("req") + val + willBeNull;
        }

        public String m4(@QueryParam("in") Double val) {
            return "" + val;
        }

        public String m5(ServletContext context) {
            return (String) context.getAttribute("test");
        }

        public String m6(HttpServletResponse response) {
            return response.getCharacterEncoding();
        }

        @Consumes("test/date") public java.util.Date m7(@Context java.util.Date date) {
            return date;
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    HttpServletResponse response;

    HttpSession session;

    ServletContext context;

    @BeforeEach @Override //
    protected void setUp() throws Exception {
        super.setUp();
        session = Mockito.mock(HttpSession.class);
        context = Mockito.mock(ServletContext.class);
        response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(request.getAttribute("req")).thenReturn("EVE");
        Mockito.when(request.getParameter("in")).thenReturn("120.5");
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute("test")).thenReturn("YES");
        Mockito.when(context.getAttribute("test")).thenReturn("CONTEXT");
        Mockito.when(response.getCharacterEncoding()).thenReturn("KOI8-R");
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void invokeMethod(String methodName, PathValues pathValues, Object expectedResult)
            throws ConverterException, ParametersValidationException {

        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider()).addEntryPoint(EntryPoint1.class)
                .setParametersValidator(VALIDATOR).build();

        Method method = findMethod(EntryPoint1.class, methodName);
        MethodInvoker caller = new MethodInvoker(config, EntryPoint1.class, method, DEFAULT_CONTENT_TYPES);

        Object result = caller.invoke(request, response, context, pathValues);

        assertEquals(expectedResult, result);
    }

    @Test //
    public void invokeM7() throws IOException, ConverterException, ParametersValidationException {

        BufferedReader reader = new BufferedReader(new StringReader("TO HAVE BODY"));
        Mockito.when(request.getReader()).thenReturn(reader);
        Mockito.when(request.getContentType()).thenReturn("test/date");

        Method method = findMethod(EntryPoint1.class, "m7");
        
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider()).addEntryPoint(EntryPoint1.class)
                .setParametersValidator(VALIDATOR).addConsumer("test/date", TEST_CONSUMER).build();
        
        MethodInvoker caller = new MethodInvoker(config, EntryPoint1.class, method, new String[] {"test/date"});

        java.util.Date result = (java.util.Date) caller.invoke(request, response, null, null);

        assertNotNull(result);
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("m0", PathValues.EMPTY, "OK"),
            Arguments.of("m1", new PathValues("in", "123"), 123),
            Arguments.of("m2", PathValues.EMPTY, "YES"),
            Arguments.of("m3", new PathValues("in", "123"), "EVE123null"),
            Arguments.of("m4", null, "120.5"),
            Arguments.of("m5", null, "CONTEXT"),
            Arguments.of("m6", null, "KOI8-R"));
        // @formatter:on
    }
}
