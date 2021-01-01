package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.RoutingInitializationException;

public class BodyParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {
        public java.util.Date context(java.util.Date date) {
            return date;
        }
    }

    @Test //
    public void exception() {
        Method m = findMethod(EntryPoint1.class, "context");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> new BodyParameter(m, p, HOLDER, "aa/bb"));
    }
}
