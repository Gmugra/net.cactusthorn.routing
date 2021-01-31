package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;

public class SecurityContextParameterTest extends InvokeTestAncestor {

    static final Principal PRINCIPAL = () -> {
        return "NAME";
    };

    public static class EntryPoint1 {

        public void simple(@Context SecurityContext securityContext) {
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
        ParameterInfo paramInfo = parameterInfo(EntryPoint1.class, "simple", CONFIG);
        MethodParameter mp = MethodParameter.Factory.create(paramInfo, CONFIG, DEFAULT_CONTENT_TYPES);

        Mockito.when(request.getUserPrincipal()).thenReturn(PRINCIPAL);
        Mockito.when(request.isSecure()).thenReturn(true);
        Mockito.when(request.getAuthType()).thenReturn("?");
        Mockito.when(request.isUserInRole(Mockito.any())).thenReturn(true);

        SecurityContext value = (SecurityContext) mp.findValue(request, null, null, null);
        assertEquals("NAME", value.getUserPrincipal().getName());
        assertEquals("?", value.getAuthenticationScheme());
        assertTrue(value.isSecure());
        assertTrue(value.isUserInRole("?"));
    }
}
