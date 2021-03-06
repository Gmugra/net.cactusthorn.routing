package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;

import static net.cactusthorn.routing.uri.PathTemplate.PathValues;

public class UriInfoParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void simple(@Context UriInfo uriInfo) {
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
        Mockito.when(request.getContextPath()).thenReturn("cntxt");
        Mockito.when(request.getServletPath()).thenReturn("/srvlt");
        Mockito.when(request.getPathInfo()).thenReturn("/app/xxx");
        Mockito.when(request.getHeader("Host")).thenReturn("cactusthorn.net");
        Mockito.when(request.isSecure()).thenReturn(false);
        Mockito.when(request.getParameterNames()).thenReturn(Collections.emptyEnumeration());

        MethodParameter mp = parameterInfo(EntryPoint1.class, "simple", CONFIG);

        UriInfo uriInfo = (UriInfo) mp.findValue(request, null, null, new PathValues());
        assertEquals("http://cactusthorn.net/cntxt/srvlt/app/xxx", uriInfo.getAbsolutePath().toString());
    }
}
