package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;

public class HttpHeadersParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {
        public void simple(@Context HttpHeaders headers) {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {
        @Override public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    @Test
    public void findValue() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("test-header", "test-value");
        Mockito.when(request.getHeaderNames()).thenReturn(Collections.enumeration(map.keySet()));
        Mockito.when(request.getHeaders("test-header")).thenReturn(Collections.enumeration(map.values()));

        MethodParameter mp = parameterInfo(EntryPoint1.class, "simple", CONFIG);
        HttpHeaders result = (HttpHeaders) mp.findValue(request, null, null, null);
        assertEquals("test-value", result.getHeaderString("test-header"));
    }
}
