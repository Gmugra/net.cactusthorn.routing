package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import net.cactusthorn.routing.convert.ConvertersHolder;

public class InvokeTestAncestor {

    protected final static ConvertersHolder HOLDER = new ConvertersHolder();

    protected HttpServletRequest request;

    @BeforeEach //
    protected void setUp() throws Exception {
        request = Mockito.mock(HttpServletRequest.class);
    }

    protected Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }
}
