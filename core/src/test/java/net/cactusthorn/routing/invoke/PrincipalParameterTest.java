package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.Principal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class PrincipalParameterTest extends InvokeTestAncestor {

    static final Principal PRINCIPAL = () -> {
        return "NAME";
    };

    public static class EntryPoint1 {

        public void simple(Principal principal) {
        }
    }

    @Test //
    public void simple() throws Exception {
        Method m = findMethod(EntryPoint1.class, "simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getUserPrincipal()).thenReturn(PRINCIPAL);
        Principal value = (Principal) mp.findValue(request, null, null, null);
        assertEquals("NAME", value.getName());
    }
}
