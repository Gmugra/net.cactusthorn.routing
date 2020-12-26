package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.HeaderParam;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class HeaderParamParameterTest {

    static final ConvertersHolder HOLDER = new ConvertersHolder();

    public static class EntryPoint1 {

        public void simple(@HeaderParam("val") String value) {
        }

        public void simpleArray(@HeaderParam("val") String[] values) {
        }
    }

    HttpServletRequest request;

    @BeforeEach //
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test //
    public void simple() throws ConverterException {
        Method m = findMethod("simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getHeader("val")).thenReturn("xyz");
        String header = (String) mp.findValue(request, null, null, null);
        assertEquals("xyz", header);
    }

    @Test //
    public void simpleArray() throws ConverterException {
        Method m = findMethod("simpleArray");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
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
