package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
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
    public void simple() throws Exception {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Cookie[] cookies = new Cookie[] { new Cookie("val", "xyz") };

        Mockito.when(request.getCookies()).thenReturn(cookies);
        Cookie cookie = (Cookie) mp.findValue(request, null, null, null);
        assertEquals("xyz", cookie.getValue());
    }

    @Test //
    public void wrongType() throws Exception {
        Method m = findMethod(EntryPoint1.class, "wrongType");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @Test //
    public void nullCookies() throws Exception {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getCookies()).thenReturn(null);
        Cookie cookie = (Cookie) mp.findValue(request, null, null, null);
        assertNull(cookie);
    }

    @Test //
    public void nullCookie() throws Exception {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Cookie[] cookies = new Cookie[] { new Cookie("xxx", "xxx") };

        Mockito.when(request.getCookies()).thenReturn(cookies);
        Cookie cookie = (Cookie) mp.findValue(request, null, null, null);
        assertNull(cookie);
    }
}
