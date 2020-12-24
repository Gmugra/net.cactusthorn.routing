package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class PathParamParameterTest {

    static final ConvertersHolder HOLDER = new ConvertersHolder();

    public static class EntryPoint1 {

        public void array(@PathParam("val") int[] values) {
        }
    }

    @Test //
    public void array() {
        Method m = findMethod("array");
        Parameter p = m.getParameters()[0];
        assertThrows(IllegalArgumentException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
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
