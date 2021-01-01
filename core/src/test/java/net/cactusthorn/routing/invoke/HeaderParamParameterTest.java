package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.DefaultValue;
import net.cactusthorn.routing.annotation.HeaderParam;

public class HeaderParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void simple(@HeaderParam("val") String value) {
        }

        public void simpleArray(@HeaderParam("val") String[] values) {
        }

        public void defaultValue(@HeaderParam("val") @DefaultValue("D") String value) {
        }
    }

    @Test //
    public void simple() throws Exception {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getHeader("val")).thenReturn("xyz");
        String header = (String) mp.findValue(request, null, null, null);
        assertEquals("xyz", header);
    }

    @Test //
    public void defaultValue() throws Exception {
        Method m = findMethod(EntryPoint1.class, "defaultValue");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getHeader("val")).thenReturn(null);
        String header = (String) mp.findValue(request, null, null, null);
        assertEquals("D", header);
    }

    @Test //
    public void simpleArray() {
        Method m = findMethod(EntryPoint1.class, "simpleArray");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }
}
