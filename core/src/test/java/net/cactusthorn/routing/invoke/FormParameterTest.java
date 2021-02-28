package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingInitializationException;

public class FormParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        @POST public void wrongConsumes(Form form) {
        }

        @POST @Consumes(MediaType.APPLICATION_FORM_URLENCODED) //
        public void form(Form form) {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {
        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    @Test public void wrongContentType() {
        assertThrows(RoutingInitializationException.class, () -> parameterInfo(EntryPoint1.class, "wrongConsumes", CONFIG));
    }

    @Test public void form() throws Exception {
        MethodParameter mp = parameterInfo(EntryPoint1.class, "form", CONFIG);

        Map<String, String[]> parameters = new HashMap<>();
        parameters.put("aaaa", new String[] { "super value" });

        Mockito.when(request.getParameterNames()).thenReturn(Collections.enumeration(parameters.keySet()));
        Mockito.when(request.getParameterValues("aaaa")).thenReturn(parameters.get("aaaa"));

        Form f = (Form) mp.findValue(request, null, null, null);
        assertEquals("super value", f.asMap().getFirst("aaaa"));
    }
}
