package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;

public class CookieParamParameterTest extends InvokeTestAncestor {

    @Path("/test") //
    public static class EntryPoint1 {

        public void simple(@CookieParam("val") String value) {
        }

        public void primitive(@CookieParam("val") int value) {
        }

        public void simpleCollection(@CookieParam("val") @DefaultValue("100") List<Integer> values) {
        }

        public void cookie(@CookieParam("val") @DefaultValue("abc") Cookie value) {
        }

        public void cookieCollection(@CookieParam("val") @DefaultValue("abc") List<Cookie> value) {
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
    public void simple() throws Exception {
        Cookie cookie = new Cookie("val", "ab cd");
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn(cookie.toString());
        MethodParameter mp = parameterInfo(EntryPoint1.class, "simple", CONFIG);
        String result = (String) mp.findValue(request, null, null, null);
        assertEquals("ab cd", result);
    }

    @Test //
    public void simpleNull() throws Exception {
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn(null);
        MethodParameter mp = parameterInfo(EntryPoint1.class, "simple", CONFIG);
        String result = (String) mp.findValue(request, null, null, null);
        assertNull(result);
    }

    @Test //
    public void convertingException() throws Exception {
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn("val=aaa");
        MethodParameter mp = parameterInfo(EntryPoint1.class, "primitive", CONFIG);
        assertThrows(BadRequestException.class, () -> mp.findValue(request, null, null, null));
    }

    @Test //
    public void primitive() throws Exception {
        Cookie cookie = new Cookie("val", "20");
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn(cookie.toString());
        MethodParameter mp = parameterInfo(EntryPoint1.class, "primitive", CONFIG);
        int result = (int) mp.findValue(request, null, null, null);
        assertEquals(20, result);
    }

    @Test //
    public void primitiveNotExists() throws Exception {
        Cookie cookie = new Cookie("xxx", "yyy");
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn(cookie.toString());
        MethodParameter mp = parameterInfo(EntryPoint1.class, "primitive", CONFIG);
        int result = (int) mp.findValue(request, null, null, null);
        assertEquals(0, result);
    }

    @Test //
    public void simpleCollection() throws Exception {
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn("val=30; cc=wert, val=40");
        MethodParameter mp = parameterInfo(EntryPoint1.class, "simpleCollection", CONFIG);
        List<?> result = (List<?>) mp.findValue(request, null, null, null);
        assertEquals(2, result.size());
        assertEquals(30, result.get(0));
        assertEquals(40, result.get(1));
    }

    @Test //
    public void simpleCollectionNullDefault() throws Exception {
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn(null);
        MethodParameter mp = parameterInfo(EntryPoint1.class, "simpleCollection", CONFIG);
        List<?> result = (List<?>) mp.findValue(request, null, null, null);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0));
    }

    @Test //
    public void simpleCollectionEmptyDefault() throws Exception {
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn("cc=wert");
        MethodParameter mp = parameterInfo(EntryPoint1.class, "simpleCollection", CONFIG);
        List<?> result = (List<?>) mp.findValue(request, null, null, null);
        assertEquals(1, result.size());
        assertEquals(100, result.get(0));
    }

    @Test //
    public void cookie() throws Exception {
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn("cc=wert; val=40");
        MethodParameter mp = parameterInfo(EntryPoint1.class, "cookie", CONFIG);
        Cookie result = (Cookie) mp.findValue(request, null, null, null);
        assertEquals("40", result.getValue());
    }

    @Test //
    public void cookieDefault() throws Exception {
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn("cc=wert");
        MethodParameter mp = parameterInfo(EntryPoint1.class, "cookie", CONFIG);
        Cookie result = (Cookie) mp.findValue(request, null, null, null);
        assertEquals("val", result.getName());
        assertEquals("abc", result.getValue());
    }

    @Test @SuppressWarnings("unchecked") //
    public void cookieCollection() throws Exception {
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn("val=30; cc=wert, val=40");
        MethodParameter mp = parameterInfo(EntryPoint1.class, "cookieCollection", CONFIG);
        List<Cookie> result = (List<Cookie>) mp.findValue(request, null, null, null);
        assertEquals(2, result.size());
        assertEquals("30", result.get(0).getValue());
        assertEquals("40", result.get(1).getValue());
    }

    @Test @SuppressWarnings("unchecked") //
    public void cookieCollectionDefault() throws Exception {
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn("cc=wert");
        MethodParameter mp = parameterInfo(EntryPoint1.class, "cookieCollection", CONFIG);
        List<Cookie> result = (List<Cookie>) mp.findValue(request, null, null, null);
        assertEquals(1, result.size());
        assertEquals("abc", result.get(0).getValue());
    }

    @Test //
    public void cookieDefaultName() throws Exception {
        Mockito.when(request.getHeader(HttpHeaders.COOKIE)).thenReturn("cc=wert; value=40");
        MethodParameter mp = parameterInfo(EntryPoint1.class, "byName", CONFIG);
        Cookie result = (Cookie) mp.findValue(request, null, null, null);
        assertEquals("40", result.getValue());
    }
}
