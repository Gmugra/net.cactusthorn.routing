package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.*;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.converter.ConvertersHolder;

public class ConsumesTest {

    private static final ConvertersHolder HOLDER = new ConvertersHolder();

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
        EntryPointScanner scanner = new EntryPointScanner(Arrays.asList(EntryPoint1.class), new EntryPoint1Provider1(), HOLDER);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);
        assertEquals("*/*", entryPoint.consumes());
        assertTrue(entryPoint.matchContentType("aaa/vvvv"));
    }

    @Test //
    public void post() {
        EntryPointScanner scanner = new EntryPointScanner(Arrays.asList(EntryPoint1.class), new EntryPoint1Provider1(), HOLDER);
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

        @POST //
        public void text() {
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
        EntryPointScanner scanner = new EntryPointScanner(Arrays.asList(EntryPoint2.class), new EntryPoint1Provider2(), HOLDER);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(POST.class).get(0);
        assertTrue(entryPoint.matchContentType("text/html"));
        assertFalse(entryPoint.matchContentType("application/json"));
    }

    @Test //
    public void override() {
        EntryPointScanner scanner = new EntryPointScanner(Arrays.asList(EntryPoint2.class), new EntryPoint1Provider2(), HOLDER);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);
        assertFalse(entryPoint.matchContentType("text/html"));
        assertTrue(entryPoint.matchContentType("application/json"));
    }
}
