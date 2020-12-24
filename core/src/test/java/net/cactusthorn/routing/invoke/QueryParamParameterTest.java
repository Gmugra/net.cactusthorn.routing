package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class QueryParamParameterTest {

    static final ConvertersHolder HOLDER = new ConvertersHolder();

    public static class EntryPoint1 {

        public void array(@QueryParam("val") int[] values) {
        }

        public void multiArray(@QueryParam("val") int[][] values) {
        }

        public void wrong(@QueryParam("val") int values) {
        }
    }

    @Test //
    public void array() throws ConverterException {
        Method m = findMethod("array");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameterValues("val")).thenReturn(new String[] { "100", "200" });
        RequestData data = new RequestData(null);

        int[] result = (int[]) mp.findValue(request, null, null, data);

        assertArrayEquals(new int[] { 100, 200 }, result);
    }

    @Test //
    public void multiArray() {
        Method m = findMethod("multiArray");
        Parameter p = m.getParameters()[0];
        assertThrows(IllegalArgumentException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @Test //
    public void wrong() throws ConverterException {
        Method m = findMethod("wrong");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameter("val")).thenReturn("abc");
        RequestData data = new RequestData(null);

        assertThrows(ConverterException.class, () -> mp.findValue(request, null, null, data));
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
