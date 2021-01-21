package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingInitializationException;

public class HeaderParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void simple(@HeaderParam("val") String value) {
        }

        public void byName(@HeaderParam("") String val) {
        }

        public void simpleArray(@HeaderParam("val") String[] values) {
        }

        public void defaultValue(@HeaderParam("val") @DefaultValue("D") String value) {
        }

        public void nullValue(@HeaderParam("val") String value) {
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
        Method m = findMethod(EntryPoint1.class, "simpleArray");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, null, CONFIG, DEFAULT_CONTENT_TYPES));
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void headerValue(String methodName, String requestValue, String expectedValue) throws Exception {
        Method m = findMethod(EntryPoint1.class, methodName);
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, null, CONFIG, DEFAULT_CONTENT_TYPES);

        Mockito.when(request.getHeader("val")).thenReturn(requestValue);
        String header = (String) mp.findValue(request, null, null, null);
        assertEquals(expectedValue, header);
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("simple", "xyz", "xyz"),
            Arguments.of("byName", "xyz", "xyz"),
            Arguments.of("defaultValue", null, "D"),
            Arguments.of("defaultValue", "xyz", "xyz"),
            Arguments.of("nullValue", null, null));
        // @formatter:on
    }
}
