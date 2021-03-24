package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;

public class ApplicationParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {
        @GET public void application(@Context Application application) {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {
        @Override public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class)
            .putApplicationProperties("P", "V").build();

    @Test public void doit() throws Exception {
        MethodParameter body = parameterInfo(EntryPoint1.class, "application", CONFIG);
        Application application = (Application) body.findValue(null, null, null, null);
        assertEquals("V", application.getProperties().get("P"));
    }
}
