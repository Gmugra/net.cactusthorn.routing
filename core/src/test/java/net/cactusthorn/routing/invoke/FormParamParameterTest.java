package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingInitializationException;

public class FormParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void simple(@FormParam("val") String value) {
        }

        public void defaultValue(@FormParam("val") @DefaultValue("D") String value) {
        }

        public void defaultList(@FormParam("val") @DefaultValue("A") List<String> value) {
        }

        public void byName(@FormParam("") String val) {
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
    public void wrongContentType() {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, null, CONFIG, DEFAULT_CONTENT_TYPES));
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void findFormValue(String methodName, String requestValue, Object expectedValue) throws Exception {
        Method m = findMethod(EntryPoint1.class, methodName);
        Parameter p = m.getParameters()[0];
        Set<MediaType> consumesMediaTypes = new HashSet<>();
        consumesMediaTypes.add(MediaType.APPLICATION_FORM_URLENCODED_TYPE);
        MethodParameter mp = MethodParameter.Factory.create(m, p, m.getGenericParameterTypes()[0], CONFIG, consumesMediaTypes);

        Mockito.when(request.getParameter("val")).thenReturn(requestValue);
        if (requestValue != null) {
            Mockito.when(request.getParameterValues("val")).thenReturn(new String[] { requestValue });
        }
        Object value = mp.findValue(request, null, null, null);
        
        if (List.class.isAssignableFrom(value.getClass())) {
            @SuppressWarnings("unchecked") List<String> list = (List<String>) value;
            assertEquals(expectedValue, list.get(0));
        } else {
            assertEquals(expectedValue, value);
        }
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            //Arguments.of("simple", "xyz", "xyz"),
            //Arguments.of("byName", "xyz", "xyz"),
            Arguments.of("defaultList", null, "A"),
            Arguments.of("defaultValue", null, "D"),
            Arguments.of("defaultValue", "xyz", "xyz"),
            Arguments.of("defaultList", "xyz", "xyz"));
        // @formatter:on
    }
}
