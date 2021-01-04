package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.DefaultValue;
import net.cactusthorn.routing.annotation.HeaderParam;

public class HeaderParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void simple(@HeaderParam("val") String value) {
        }

        public void simpleArray(@HeaderParam("val") String[] values) {
        }

        public void defaultValue(@HeaderParam("val") @DefaultValue("D") String value) {
        }
    }

    @Test //
    public void simpleArray() {
        Method m = findMethod(EntryPoint1.class, "simpleArray");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void headerValue(String methodName, String requestValue, String expectedValue) throws Exception {
        Method m = findMethod(EntryPoint1.class, methodName);
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getHeader("val")).thenReturn(requestValue);
        String header = (String) mp.findValue(request, null, null, null);
        assertEquals(expectedValue, header);
    }

    private static Stream<Arguments> provideArguments() {
        return Stream.of(Arguments.of("simple", "xyz", "xyz"), Arguments.of("defaultValue", null, "D"));
    }
}
