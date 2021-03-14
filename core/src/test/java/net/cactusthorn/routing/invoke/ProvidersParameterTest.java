package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Providers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.body.reader.InputStreamMessageBodyReader;
import net.cactusthorn.routing.util.ProvidersImpl;

public class ProvidersParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {
        @GET public void providers(@Context Providers providers) {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {
        @Override public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    @BeforeAll protected static void beforeAll() throws Exception {
        ((ProvidersImpl) CONFIG.providers()).init(null, CONFIG);
    }

    @Test
    public void doit() throws Exception {
        MethodParameter body = parameterInfo(EntryPoint1.class, "providers", CONFIG);
        Providers providers = (Providers) body.findValue(null, null, null, null);
        assertEquals(InputStreamMessageBodyReader.class, providers.getMessageBodyReader(InputStream.class, null, null, null).getClass());
    }
}
