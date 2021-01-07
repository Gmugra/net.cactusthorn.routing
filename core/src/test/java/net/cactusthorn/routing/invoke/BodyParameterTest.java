package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.Consumer;
import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.Consumes;
import net.cactusthorn.routing.annotation.Context;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class BodyParameterTest extends InvokeTestAncestor {

    public static final Consumer TEST_CONSUMER = (clazz, mediaType, data) -> {
        return new java.util.Date();
    };

    public static class EntryPoint1 {
        @Consumes("test/date") public java.util.Date context(@Context java.util.Date date) {
            return date;
        }
    }

    @Test //
    public void exception() {
        Method m = findMethod(EntryPoint1.class, "context");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> new BodyParameter(m, p, HOLDER, new String[] {"aa/bb"}));
    }

    @Test //
    public void ok() throws Exception {
        Mockito.when(request.getContentType()).thenReturn("test/date");

        ConvertersHolder holder = new ConvertersHolder();
        holder.register("test/date", TEST_CONSUMER);
        Method m = findMethod(EntryPoint1.class, "context");
        Parameter p = m.getParameters()[0];
        BodyParameter body = new BodyParameter(m, p, holder, new String[] {"test/date"});
        java.util.Date date = (java.util.Date)body.findValue(request, null, null, null);
        assertNotNull(date);
    }

    @Test //
    public void requestNull() throws Exception {
        Mockito.when(request.getContentType()).thenReturn(null);

        ConvertersHolder holder = new ConvertersHolder();
        holder.register("test/date", TEST_CONSUMER);
        Method m = findMethod(EntryPoint1.class, "context");
        Parameter p = m.getParameters()[0];
        BodyParameter body = new BodyParameter(m, p, holder, new String[] {"test/date"});
        java.util.Date date = (java.util.Date)body.findValue(request, null, null, null);
        assertNull(date);
    }

    @Test //
    public void requestEmpty() throws Exception {
        Mockito.when(request.getContentType()).thenReturn("  ");

        ConvertersHolder holder = new ConvertersHolder();
        holder.register("test/date", TEST_CONSUMER);
        Method m = findMethod(EntryPoint1.class, "context");
        Parameter p = m.getParameters()[0];
        BodyParameter body = new BodyParameter(m, p, holder, new String[] {"test/date"});
        java.util.Date date = (java.util.Date)body.findValue(request, null, null, null);
        assertNull(date);
    }
}
