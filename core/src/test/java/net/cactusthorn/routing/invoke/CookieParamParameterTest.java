package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.CookieParam;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class CookieParamParameterTest {

    static final ConvertersHolder HOLDER = new ConvertersHolder();

    public static class EntryPoint1 {

        public void simple(@CookieParam("val") Cookie value) {
        }

        public void wrongType(@CookieParam("val") String value) {
        }
    }

    HttpServletRequest request;

    @BeforeEach //
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test //
    public void simple() throws Exception {
        Method m = findMethod("simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Cookie[] cookies = new Cookie[] { new Cookie("val", "xyz") };

        Mockito.when(request.getCookies()).thenReturn(cookies);
        Cookie cookie = (Cookie) mp.findValue(request, null, null, null);
        assertEquals("xyz", cookie.getValue());
    }

    @Test //
    public void wrongType() throws Exception {
        Method m = findMethod("wrongType");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @Test //
    public void nullCookies() throws Exception {
        Method m = findMethod("simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getCookies()).thenReturn(null);
        Cookie cookie = (Cookie) mp.findValue(request, null, null, null);
        assertNull(cookie);
    }

    @Test //
    public void nullCookie() throws Exception {
        Method m = findMethod("simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Cookie[] cookies = new Cookie[] { new Cookie("xxx", "xxx") };

        Mockito.when(request.getCookies()).thenReturn(cookies);
        Cookie cookie = (Cookie) mp.findValue(request, null, null, null);
        assertNull(cookie);
    }

    private Method findMethod(String methodName) {
        for (Method method : EntryPoint1.class.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }
}
