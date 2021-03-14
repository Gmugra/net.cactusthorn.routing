package net.cactusthorn.routing.util;

import static org.junit.jupiter.api.Assertions.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ext.Providers;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;

public class ProvidersImplTest {

    public static class EntryPoint1Provider implements ComponentProvider {
        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return null;
        }
    }

    static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).build();

    @Test public void getContextResolver() {
        Providers providers = CONFIG.providers();
        assertThrows(UnsupportedOperationException.class, () -> providers.getContextResolver(null, null));
    }
}
