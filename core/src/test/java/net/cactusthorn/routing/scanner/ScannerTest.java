package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.EntryPointScanner;
import net.cactusthorn.routing.PathTemplate;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class ScannerTest {

    @Path("/api") //
    public static class EntryPoint1 {

        @GET @Path("dddd") //
        public void m1() {
        }

        @GET @Path("/dddd") //
        public void m2() {
        }

        @GET @Path("dddd/") public void m3() {
        }

        @GET @Path("/dddd/") public void m4() {
        }
    }

    public static class EntryPoint1Provider1 implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new EntryPoint1();
        }
    }

    static final ConvertersHolder HOLDER = new ConvertersHolder();
    static Map<ConfigProperty, Object> PROPERTIES = new HashMap<>();

    @BeforeAll //
    static void setUp() {
        PROPERTIES.put(ConfigProperty.READ_BODY_BUFFER_SIZE, 512);
    }

    @Test //
    public void entryPoint1() {
        EntryPointScanner f = new EntryPointScanner(Arrays.asList(EntryPoint1.class), new EntryPoint1Provider1(), HOLDER, PROPERTIES);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        List<EntryPoint> gets = entryPoints.get(GET.class);

        for (EntryPoint entryPoint : gets) {
            assertTrue(entryPoint.match("/api/dddd/"));
        }
    }

    public static class EntryPoint2 {

        @GET @Path("dddd") //
        public void m1() {
        }

        @GET @Path("/dddd") //
        public void m2() {
        }

        @GET @Path("dddd/") public void m3() {
        }

        @GET @Path("/dddd/") public void m4() {
        }
    }

    public static class EntryPoint1Provider2 implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new EntryPoint2();
        }
    }

    @Test //
    public void entryPoint2() {
        EntryPointScanner f = new EntryPointScanner(Arrays.asList(EntryPoint2.class), new EntryPoint1Provider2(), HOLDER, PROPERTIES);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        List<EntryPoint> gets = entryPoints.get(GET.class);

        for (EntryPoint entryPoint : gets) {
            assertTrue(entryPoint.match("/dddd/"));
        }
    }

    public static class EntryPoint3 {

        @GET public void m1() {
        }
    }

    public static class EntryPoint1Provider3 implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new EntryPoint3();
        }
    }

    @Test //
    public void entryPoint3() {
        EntryPointScanner f = new EntryPointScanner(Arrays.asList(EntryPoint3.class), new EntryPoint1Provider3(), HOLDER, PROPERTIES);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);

        assertTrue(entryPoint.match("/"));
        assertEquals(EntryPointScanner.PRODUCES_DEFAULT, entryPoint.produces());
    }

    @Path("/api") //
    public static class EntryPoint4 {

        @GET @Produces("*/*") public void m1() {
        }
    }

    public static class EntryPoint1Provider4 implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz) {
            return new EntryPoint4();
        }
    }

    @Test //
    public void entryPoint4() {
        EntryPointScanner f = new EntryPointScanner(Arrays.asList(EntryPoint4.class), new EntryPoint1Provider4(), HOLDER, PROPERTIES);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);
        PathTemplate.PathValues values = entryPoint.parse("/api/");

        assertEquals(PathValues.EMPTY, values);
        assertTrue(entryPoint.match("/api/"));
        assertEquals("*/*", entryPoint.produces());
    }
}
