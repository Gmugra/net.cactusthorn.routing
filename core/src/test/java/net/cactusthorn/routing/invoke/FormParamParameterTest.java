package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.DefaultValue;
import net.cactusthorn.routing.annotation.FormParam;
import net.cactusthorn.routing.convert.ConverterException;

public class FormParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void simple(@FormParam("val") String value) {
        }

        public void defaultValue(@FormParam("val") @DefaultValue("D") String value) {
        }

        public void defaultArray(@FormParam("val") @DefaultValue("A") String[] value) {
        }
    }

    @Test //
    public void simple() throws Exception {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "application/x-www-form-urlencoded");

        Mockito.when(request.getParameter("val")).thenReturn("xyz");
        String value = (String) mp.findValue(request, null, null, null);
        assertEquals("xyz", value);
    }

    @Test //
    public void defaultArray() throws Exception {
        Method m = findMethod(EntryPoint1.class, "defaultArray");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "application/x-www-form-urlencoded");

        Mockito.when(request.getParameter("val")).thenReturn(null);
        String[] values = (String[]) mp.findValue(request, null, null, null);
        assertEquals("A", values[0]);
        assertEquals(1, values.length);
    }

    @Test //
    public void defaultValue() throws Exception {
        Method m = findMethod(EntryPoint1.class, "defaultValue");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "application/x-www-form-urlencoded");

        Mockito.when(request.getParameter("val")).thenReturn(null);
        String value = (String) mp.findValue(request, null, null, null);
        assertEquals("D", value);
    }

    @Test //
    public void wrongContentType() throws ConverterException {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }
}
