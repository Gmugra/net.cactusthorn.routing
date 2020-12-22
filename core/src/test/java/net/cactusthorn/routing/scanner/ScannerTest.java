package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.EntryPointScanner;
import net.cactusthorn.routing.Template;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.Template.PathValues;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.converter.ConvertersHolder;

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

    private static final ConvertersHolder HOLDER = new ConvertersHolder();

    @Test //
    public void entryPoint1() {
        EntryPointScanner f = new EntryPointScanner(Arrays.asList(EntryPoint1.class), new EntryPoint1Provider1(), HOLDER);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        List<EntryPoint> gets = entryPoints.get(GET.class);

        for (EntryPoint entryPoint : gets) {
            assertTrue(entryPoint.match("/api/dddd"));
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
        EntryPointScanner f = new EntryPointScanner(Arrays.asList(EntryPoint2.class), new EntryPoint1Provider2(), HOLDER);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        List<EntryPoint> gets = entryPoints.get(GET.class);

        for (EntryPoint entryPoint : gets) {
            assertTrue(entryPoint.match("/dddd"));
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
        EntryPointScanner f = new EntryPointScanner(Arrays.asList(EntryPoint3.class), new EntryPoint1Provider3(), HOLDER);
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
        EntryPointScanner f = new EntryPointScanner(Arrays.asList(EntryPoint4.class), new EntryPoint1Provider4(), HOLDER);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);
        Template.PathValues values = entryPoint.parse("/api");

        assertEquals(PathValues.EMPTY, values);
        assertTrue(entryPoint.match("/api"));
        assertEquals("*/*", entryPoint.produces());
    }
}
