package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;

public class PrincipalParameterTest extends InvokeTestAncestor {

    static final Principal PRINCIPAL = () -> {
        return "NAME";
    };

    public static class EntryPoint1 {

        public void simple(@Context Principal principal) {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    @Test //
    public void simple() throws Exception {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, null, CONFIG, DEFAULT_CONTENT_TYPES);

        Mockito.when(request.getUserPrincipal()).thenReturn(PRINCIPAL);
        Principal value = (Principal) mp.findValue(request, null, null, null);
        assertEquals("NAME", value.getName());
    }
}
