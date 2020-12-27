package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.*;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.convert.ConvertersHolder;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;

public class ConsumesTest {

    static final ConvertersHolder HOLDER = new ConvertersHolder();
    static Map<ConfigProperty, Object> PROPERTIES = new HashMap<>();

    @BeforeAll //
    static void setUp() {
        PROPERTIES.put(ConfigProperty.READ_BODY_BUFFER_SIZE, 512);
    }

    @Path("/") //
    public static class EntryPoint1 {

        @GET //
        public void all() {
        }

        @POST @Consumes("text/*") //
        public void text() {
        }
    }

    public static class EntryPoint1Provider1 implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new EntryPoint1();
        }
    }

    @Test //
    public void all() {
        EntryPointScanner scanner = new EntryPointScanner(Arrays.asList(EntryPoint1.class), new EntryPoint1Provider1(), HOLDER, PROPERTIES);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);
        assertEquals("*/*", entryPoint.consumes());
        assertTrue(entryPoint.matchContentType("aaa/vvvv"));
    }

    @Test //
    public void post() {
        EntryPointScanner scanner = new EntryPointScanner(Arrays.asList(EntryPoint1.class), new EntryPoint1Provider1(), HOLDER, PROPERTIES);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(POST.class).get(0);
        assertTrue(entryPoint.matchContentType("text/html"));
        assertFalse(entryPoint.matchContentType("application/json"));
    }

    @Path("/") @Consumes("text/*") //
    public static class EntryPoint2 {

        @GET @Consumes("application/json") //
        public void all() {
        }

        @PUT //
        public void text() {
        }

        @POST @Consumes("multipart/form-data") //
        public void formdata() {
        }
    }

    public static class EntryPoint1Provider2 implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new EntryPoint1();
        }
    }

    @Test //
    public void global() {
        EntryPointScanner scanner = new EntryPointScanner(Arrays.asList(EntryPoint2.class), new EntryPoint1Provider2(), HOLDER, PROPERTIES);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(PUT.class).get(0);
        assertTrue(entryPoint.matchContentType("text/html"));
        assertFalse(entryPoint.matchContentType("application/json"));
    }

    @Test //
    public void override() {
        EntryPointScanner scanner = new EntryPointScanner(Arrays.asList(EntryPoint2.class), new EntryPoint1Provider2(), HOLDER, PROPERTIES);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);
        assertFalse(entryPoint.matchContentType("text/html"));
        assertTrue(entryPoint.matchContentType("application/json"));
    }

    @Test //
    public void formData() {
        EntryPointScanner scanner = new EntryPointScanner(Arrays.asList(EntryPoint2.class), new EntryPoint1Provider2(), HOLDER, PROPERTIES);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(POST.class).get(0);
        assertTrue(entryPoint.matchContentType("multipart/form-data; boundary=----WebKitFormBoundaryqoNsVh2QtLJ19YqS"));
    }
}
