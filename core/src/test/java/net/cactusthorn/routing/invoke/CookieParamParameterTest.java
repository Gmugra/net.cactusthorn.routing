package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.CookieParam;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingInitializationException;

public class CookieParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void simple(@CookieParam("val") Cookie value) {
        }

        public void wrongType(@CookieParam("val") String value) {
        }

        public void byName(@CookieParam("") Cookie value) {
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
    public void wrongType() throws Exception {
        assertThrows(RoutingInitializationException.class, () -> parameterInfo(EntryPoint1.class, "wrongType", CONFIG));
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void findCookieValue(String methodName, Cookie[] expectedCookie, boolean expectedNull) throws Exception {
        MethodParameter mp = parameterInfo(EntryPoint1.class, methodName, CONFIG);

        Mockito.when(request.getCookies()).thenReturn(expectedCookie);
        Cookie cookie = (Cookie) mp.findValue(request, null, null, null);

        if (expectedNull) {
            assertNull(cookie);
        } else {
            assertEquals(expectedCookie[0].getValue(), cookie.getValue());
        }
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("simple", null, true),
            Arguments.of("simple", new Cookie[] {new Cookie("xxx", "xxx")}, true),
            Arguments.of("simple", new Cookie[] {new Cookie("val", "xyz")}, false),
            Arguments.of("byName", new Cookie[] {new Cookie("value", "xyz")}, false));
        // @formatter:on
    }
}
