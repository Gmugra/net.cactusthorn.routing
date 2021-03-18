package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingInitializationException;

public class HeaderParamParameterTest extends InvokeTestAncestor {

    @Path("/test") //
    public static class EntryPoint1 {

        public void simple(@HeaderParam("val") String value) {
        }

        public void byName(@HeaderParam("") String val) {
        }

        public void simpleArray(@HeaderParam("val") String[] values) {
        }

        public void list(@HeaderParam("val") List<Integer> values) {
        }

        public void defaultValue(@HeaderParam("val") @DefaultValue("D") String value) {
        }

        public void intValue(@HeaderParam("val") Integer value) {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    @Test //
    public void simpleArray() {
        assertThrows(RoutingInitializationException.class, () -> parameterInfo(EntryPoint1.class, "simpleArray", CONFIG));
    }

    @Test //
    public void list() throws Exception {

        List<String> list = new ArrayList<>();
        list.add("10");
        list.add("20");

        Mockito.when(request.getHeaders("val")).thenReturn(Collections.enumeration(list));

        MethodParameter mp = parameterInfo(EntryPoint1.class, "list", CONFIG);

        List<?> result = (List<?>) mp.findValue(request, null, null, null);

        assertEquals(2, result.size());
        assertEquals(10, result.get(0));
        assertEquals(20, result.get(1));
    }

    @Test //
    public void nullList() throws Exception {

        Mockito.when(request.getHeaders("val")).thenReturn(null);

        MethodParameter mp = parameterInfo(EntryPoint1.class, "list", CONFIG);

        List<?> result = (List<?>) mp.findValue(request, null, null, null);

        assertEquals(0, result.size());
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void headerValue(String methodName, String requestValue, String expectedValue) throws Exception {
        MethodParameter mp = parameterInfo(EntryPoint1.class, methodName, CONFIG);

        Mockito.when(request.getHeader("val")).thenReturn(requestValue);
        String header = (String) mp.findValue(request, null, null, null);
        assertEquals(expectedValue, header);
    }

    @Test //
    public void convertingException() throws Exception {
        MethodParameter mp = parameterInfo(EntryPoint1.class, "intValue", CONFIG);

        Mockito.when(request.getHeader("val")).thenReturn("aaa");
        assertThrows(BadRequestException.class, () -> mp.findValue(request, null, null, null));
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("simple", "xyz", "xyz"),
            Arguments.of("byName", "xyz", "xyz"),
            Arguments.of("defaultValue", null, "D"),
            Arguments.of("defaultValue", "xyz", "xyz"),
            Arguments.of("intValue", null, null));
        // @formatter:on
    }
}