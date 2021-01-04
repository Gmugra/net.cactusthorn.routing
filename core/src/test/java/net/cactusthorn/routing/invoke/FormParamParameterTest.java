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
import net.cactusthorn.routing.annotation.FormParam;
import net.cactusthorn.routing.convert.ConverterException;

public class FormParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void simple(@FormParam("val") String value) {
        }

        public void defaultValue(@FormParam("val") @DefaultValue("D") String value) {
        }

        public void defaultArray(@FormParam("val") @DefaultValue("A") String[] value) {
        }
    }

    @Test //
    public void wrongContentType() throws ConverterException {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void findFormValue(String methodName, String requestValue, Object expectedValue) throws Exception {
        Method m = findMethod(EntryPoint1.class, methodName);
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "application/x-www-form-urlencoded");

        Mockito.when(request.getParameter("val")).thenReturn(requestValue);
        Object value = mp.findValue(request, null, null, null);
        if (value.getClass().isArray()) {
            assertEquals(expectedValue, ((Object[]) value)[0]);
        } else {
            assertEquals(expectedValue, value);
        }
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("simple", "xyz", "xyz"),
            Arguments.of("defaultArray", null, "A"),
            Arguments.of("defaultValue", null, "D"));
        // @formatter:on
    }
}
