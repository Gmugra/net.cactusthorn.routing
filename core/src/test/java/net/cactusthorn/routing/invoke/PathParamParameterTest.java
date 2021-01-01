package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.annotation.DefaultValue;
import net.cactusthorn.routing.annotation.PathParam;

public class PathParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void array(@PathParam("val") int[] values) {
        }

        @SuppressWarnings("rawtypes") public void wrongCollection(@PathParam("val") List values) {
        }

        public void collection(@PathParam("val") List<String> values) {
        }

        public void math(@PathParam("val") Math values) {
        }

        public void defaultValue(@PathParam("val") @DefaultValue("10") int value) {
        }

        public void simple(@PathParam("val") int value) {
        }
    }

    @Test //
    public void array() {
        Method m = findMethod(EntryPoint1.class, "array");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @Test //
    public void collection() {
        Method m = findMethod(EntryPoint1.class, "collection");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @Test //
    public void math() {
        Method m = findMethod(EntryPoint1.class, "math");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @Test //
    public void defaultValue() throws Exception {
        Method m = findMethod(EntryPoint1.class, "defaultValue");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        PathValues values = new PathValues();
        values.put("val", "");
        RequestData requestData = new RequestData(values);

        int result = (int) mp.findValue(null, null, null, requestData);

        assertEquals(10, result);
    }

    @Test //
    public void simple() throws Exception {
        Method m = findMethod("simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        PathValues values = new PathValues();
        values.put("val", "");
        RequestData requestData = new RequestData(values);

        int result = (int) mp.findValue(null, null, null, requestData);

        assertEquals(0, result);
    }

    @Test //
    public void simpleWithValue() throws Exception {
        Method m = findMethod("simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        PathValues values = new PathValues();
        values.put("val", "20");
        RequestData requestData = new RequestData(values);

        int result = (int) mp.findValue(null, null, null, requestData);

        assertEquals(20, result);
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
