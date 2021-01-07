package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.EntryPointScanner;
import net.cactusthorn.routing.PathTemplate;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.annotation.*;

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
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    @Test //
    public void entryPoint1() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider1()).addEntryPoint(EntryPoint1.class).build();
        EntryPointScanner f = new EntryPointScanner(config);
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

        @GET @Path("dddd") public void m5() {
        }
    }

    public static class EntryPoint1Provider2 implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint2();
        }
    }

    @Test //
    public void entryPoint2() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider2()).addEntryPoint(EntryPoint2.class).build();
        EntryPointScanner f = new EntryPointScanner(config);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        List<EntryPoint> gets = entryPoints.get(GET.class);

        for (EntryPoint entryPoint : gets) {
            assertTrue(entryPoint.match("/dddd/"));
        }
    }

    public static class EntryPoint3 {

        @GET @Path("/") public void m1() {
        }
    }

    public static class EntryPoint1Provider3 implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint3();
        }
    }

    @Test //
    public void entryPoint3() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider3()).addEntryPoint(EntryPoint3.class).build();
        EntryPointScanner f = new EntryPointScanner(config);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);

        assertTrue(entryPoint.match("/"));
        assertEquals(EntryPointScanner.PRODUCES_DEFAULT, entryPoint.produces());
    }

    @Path("api/") //
    public static class EntryPoint4 {

        @GET @Produces("*/*") //
        public void m1() {
        }

        @HEAD @Produces("text/html") @Template("wow.html") //
        public void template() {
        }
    }

    public static class EntryPoint1Provider4 implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint4();
        }
    }

    @Test //
    public void entryPoint4() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider4()).addEntryPoint(EntryPoint4.class).build();
        EntryPointScanner f = new EntryPointScanner(config);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);
        PathTemplate.PathValues values = entryPoint.parse("/api/");

        assertEquals(PathValues.EMPTY, values);
        assertTrue(entryPoint.match("/api/"));
        assertEquals("*/*", entryPoint.produces());
    }

    @Test //
    public void wow() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider4()).addEntryPoint(EntryPoint4.class).build();
        EntryPointScanner f = new EntryPointScanner(config);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        EntryPoint entryPoint = entryPoints.get(HEAD.class).get(0);
        assertEquals("wow.html", entryPoint.template());
    }
}
