package net.cactusthorn.routing.util;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

import javax.ws.rs.container.ResourceInfo;

import org.junit.jupiter.api.Test;

public class ResourceInfoImplTest {

    public static class TestIt {
        public void m1() {
            return;
        }
    }

    @Test public void simple() {
        Method method = TestIt.class.getMethods()[0];
        ResourceInfo resourceInfo = new ResourceInfoImpl(TestIt.class, method);
        assertEquals(TestIt.class, resourceInfo.getResourceClass());
        assertEquals(method, resourceInfo.getResourceMethod());
    }

    @Test public void nullClass() {
        Method method = TestIt.class.getMethods()[0];
        assertThrows(IllegalArgumentException.class, () -> new ResourceInfoImpl(null, method));

    }

    @Test public void nullMethod() {
        assertThrows(IllegalArgumentException.class, () -> new ResourceInfoImpl(TestIt.class, null));
    }
}
