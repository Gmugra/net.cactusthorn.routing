package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.convert.ConvertersHolder;

public class PrincipalParameterTest {

    static final ConvertersHolder HOLDER = new ConvertersHolder();

    static final Principal PRINCIPAL = () -> {
        return "NAME";
    };

    public static class EntryPoint1 {

        public void simple(Principal principal) {
        }
    }

    HttpServletRequest request;

    @BeforeEach //
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test //
    public void simple() throws Exception {
        Method m = findMethod("simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getUserPrincipal()).thenReturn(PRINCIPAL);
        Principal value = (Principal) mp.findValue(request, null, null, null);
        assertEquals("NAME", value.getName());
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
