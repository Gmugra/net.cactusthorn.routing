package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;

public class PathParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void array(@PathParam("val") int[] values) {
        }

        @SuppressWarnings("rawtypes") public void wrongCollection(@PathParam("val") List values) {
        }

        public void collection(@PathParam("val") List<String> values) {
        }

        public void math(@PathParam("val") Math values) {
        }

        public void defaultValue(@PathParam("val") @DefaultValue("10") int value) {
        }

        public void simple(@PathParam("val") int value) {
        }

        public void byName(@PathParam("") int value) {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    @ParameterizedTest @ValueSource(strings = { "array", "collection", "math" }) //
    public void testThrows(String method) {
        assertThrows(RoutingInitializationException.class, () -> parameterInfo(EntryPoint1.class, method, CONFIG));
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void findValue(String methodName, PathValues pathValues, Object expected) throws Exception {
        MethodParameter mp = parameterInfo(EntryPoint1.class, methodName, CONFIG);

        Object result = mp.findValue(null, null, null, pathValues);

        assertEquals(expected, result);
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("simple", new PathValues("val", "20"), 20),
            Arguments.of("byName", new PathValues("value", "20"), 20),
            Arguments.of("simple", new PathValues("val", ""), 0),
            Arguments.of("defaultValue", new PathValues("val", ""), 10),
            Arguments.of("defaultValue", new PathValues("val", "15"), 15));
        // @formatter:on
    }
}
