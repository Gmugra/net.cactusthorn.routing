package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.Template.PathValues;
import net.cactusthorn.routing.annotation.Context;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.converter.ConverterException;
import net.cactusthorn.routing.converter.ConvertersHolder;

public class MethodInvokerTest {

    public static class EntryPoint1 {

        public Integer m1(@PathParam("in") Integer val) {
            return val;
        }

        public String m0() {
            return "OK";
        }

        public String m2(@Context HttpSession session) {
            return (String) session.getAttribute("test");
        }

        public String m3(@Context HttpServletRequest request, @PathParam("in") Integer val, String willBeNull) {
            return (String) request.getAttribute("req") + val + willBeNull;
        }

        public String m4(@QueryParam("in") Double val) {
            return "" + val;
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new EntryPoint1();
        }
    }

    HttpServletRequest request;

    HttpSession session;

    static ComponentProvider provider;

    static ConvertersHolder holder;

    @BeforeAll //
    static void setUp() {
        holder = new ConvertersHolder();
        provider = new EntryPoint1Provider();
    }

    @BeforeEach //
    void mock() {
        request = Mockito.mock(HttpServletRequest.class);
        session = Mockito.mock(HttpSession.class);
        Mockito.when(request.getAttribute("req")).thenReturn("EVE");
        Mockito.when(request.getParameter("in")).thenReturn("120.5");
        Mockito.when(request.getSession(false)).thenReturn(session);
        Mockito.when(session.getAttribute("test")).thenReturn("YES");
    }

    @Test //
    public void invokeM1() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ConverterException {

        Method method = findMethod("m1");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder);

        PathValues values = new PathValues();
        values.put("in", "123");

        Integer result = (Integer) caller.invoke(request, null, null, values);

        assertEquals(123, result);
    }

    @Test //
    public void invokeM0() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ConverterException {

        Method method = findMethod("m0");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder);

        PathValues values = PathValues.EMPTY;

        String result = (String) caller.invoke(request, null, null, values);

        assertEquals("OK", result);
    }

    @Test //
    public void invokeM2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ConverterException {

        Method method = findMethod("m2");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder);

        PathValues values = PathValues.EMPTY;

        String result = (String) caller.invoke(request, null, null, values);

        assertEquals("YES", result);
    }

    @Test //
    public void invokeM3() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ConverterException {

        Method method = findMethod("m3");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder);

        PathValues values = new PathValues();
        values.put("in", "123");

        String result = (String) caller.invoke(request, null, null, values);

        assertEquals("EVE123null", result);
    }

    @Test //
    public void invokeM4() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ConverterException {

        Method method = findMethod("m4");
        MethodInvoker caller = new MethodInvoker(EntryPoint1.class, method, provider, holder);

        String result = (String) caller.invoke(request, null, null, null);

        assertEquals("120.5", result);
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
