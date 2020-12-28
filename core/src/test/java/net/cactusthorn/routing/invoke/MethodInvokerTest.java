package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.Consumer;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class MethodInvokerTest {

    public static final Consumer TEST_CONSUMER = (clazz, mediaType, data) -> {
        return new java.util.Date();
    };

    public static class EntryPoint1 {

        public Integer m1(@PathParam("in") Integer val) {
            return val;
        }

        public String m0() {
            return "OK";
        }

        public String m2(HttpSession session) {
            return (String) session.getAttribute("test");
        }

        public String m3(HttpServletRequest request, @PathParam("in") Integer val, String willBeNull) {
            return (String) request.getAttribute("req") + val + willBeNull;
        }

        public String m4(@QueryParam("in") Double val) {
            return "" + val;
        }

        public String m5(ServletContext context) {
            return (String) context.getAttribute("test");
        }

        public String m6(HttpServletResponse response) {
            return response.getCharacterEncoding();
        }

        public java.util.Date m7(@Context java.util.Date date) {
            return date;
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    static Map<ConfigProperty, Object> configProperties;

    static ComponentProvider provider;

    static ConvertersHolder holder;

    @BeforeAll //
    static void setUp() {
        holder = new ConvertersHolder();
        holder.register("test/date", TEST_CONSUMER);
        provider = new EntryPoint1Provider();
        configProperties = new HashMap<>();
        configProperties.put(ConfigProperty.READ_BODY_BUFFER_SIZE, 512);
    }

    HttpServletRequest request;

    HttpServletResponse response;

    HttpSession session;

    ServletContext context;

    @BeforeEach //
    void mock() throws IOException {
        request = Mockito.mock(HttpServletRequest.class);
        session = Mockito.mock(HttpSession.class);
        context = Mockito.mock(ServletContext.class);
        response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(request.getAttribute("req")).thenReturn("EVE");
        Mockito.when(request.getParameter("in")).thenReturn("120.5");
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute("test")).thenReturn("YES");
        Mockito.when(context.getAttribute("test")).thenReturn("CONTEXT");
        Mockito.when(response.getCharacterEncoding()).thenReturn("KOI8-R");
    }

    @Test //
    public void invokeM1() throws ConverterException {

        Method method = findMethod("m1");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder, "*/*", configProperties);

        PathValues values = new PathValues();
        values.put("in", "123");

        Integer result = (Integer) caller.invoke(request, null, null, values);

        assertEquals(123, result);
    }

    @Test //
    public void invokeM0() throws ConverterException {

        Method method = findMethod("m0");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder, "*/*", configProperties);

        PathValues values = PathValues.EMPTY;

        String result = (String) caller.invoke(request, null, null, values);

        assertEquals("OK", result);
    }

    @Test //
    public void invokeM2() throws ConverterException {

        Method method = findMethod("m2");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder, "*/*", configProperties);

        PathValues values = PathValues.EMPTY;

        String result = (String) caller.invoke(request, null, null, values);

        assertEquals("YES", result);
    }

    @Test //
    public void invokeM3() throws ConverterException {

        Method method = findMethod("m3");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder, "*/*", configProperties);

        PathValues values = new PathValues();
        values.put("in", "123");

        String result = (String) caller.invoke(request, null, null, values);

        assertEquals("EVE123null", result);
    }

    @Test //
    public void invokeM4() throws ConverterException {

        Method method = findMethod("m4");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder, "*/*", configProperties);

        String result = (String) caller.invoke(request, null, null, null);

        assertEquals("120.5", result);
    }

    @Test //
    public void invokeM5() throws ConverterException {

        Method method = findMethod("m5");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder, "*/*", configProperties);

        String result = (String) caller.invoke(request, null, context, null);

        assertEquals("CONTEXT", result);
    }

    @Test //
    public void invokeM6() throws ConverterException {

        Method method = findMethod("m6");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder, "*/*", configProperties);

        String result = (String) caller.invoke(request, response, null, null);

        assertEquals("KOI8-R", result);
    }

    @Test //
    public void invokeM7() throws IOException, ConverterException {

        BufferedReader reader = new BufferedReader(new StringReader("TO HAVE BODY"));
        Mockito.when(request.getReader()).thenReturn(reader);

        Method method = findMethod("m7");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder, "test/date", configProperties);

        java.util.Date result = (java.util.Date) caller.invoke(request, response, null, null);

        assertNotNull(result);
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
