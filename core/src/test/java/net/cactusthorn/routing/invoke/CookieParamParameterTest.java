package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.stream.Stream;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.CookieParam;

public class CookieParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void simple(@CookieParam("val") Cookie value) {
        }

        public void wrongType(@CookieParam("val") String value) {
        }
    }

    @Test //
    public void wrongType() throws Exception {
        Method m = findMethod(EntryPoint1.class, "wrongType");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void findCookieValue(String methodName, Cookie[] expectedCookie, boolean expectedNull) throws Exception {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

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
            Arguments.of("simple", new Cookie[] {new Cookie("val", "xyz")}, false));
        // @formatter:on
    }
}
