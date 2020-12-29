package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class BodyParameterTest {

    static final ConvertersHolder HOLDER = new ConvertersHolder();

    public static class EntryPoint1 {
        public java.util.Date context(java.util.Date date) {
            return date;
        }
    }

    @Test //
    public void exception() {
        Method m = findMethod("context");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> new BodyParameter(m, p, HOLDER, "aa/bb"));
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
