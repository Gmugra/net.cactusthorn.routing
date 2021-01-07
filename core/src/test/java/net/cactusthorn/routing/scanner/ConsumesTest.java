package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.*;
import net.cactusthorn.routing.annotation.*;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;

public class ConsumesTest {

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
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    @Test //
    public void all() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider1()).addEntryPoint(EntryPoint1.class).build();
        EntryPointScanner scanner = new EntryPointScanner(config);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);
        assertEquals("*/*", entryPoint.consumes());
        assertTrue(entryPoint.matchContentType("aaa/vvvv"));
    }

    @Test //
    public void post() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider1()).addEntryPoint(EntryPoint1.class).build();
        EntryPointScanner scanner = new EntryPointScanner(config);
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
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    @Test //
    public void global() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider2()).addEntryPoint(EntryPoint2.class).build();
        EntryPointScanner scanner = new EntryPointScanner(config);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(PUT.class).get(0);
        assertTrue(entryPoint.matchContentType("text/html"));
        assertFalse(entryPoint.matchContentType("application/json"));
    }

    @Test //
    public void override() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider2()).addEntryPoint(EntryPoint2.class).build();
        EntryPointScanner scanner = new EntryPointScanner(config);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(GET.class).get(0);
        assertFalse(entryPoint.matchContentType("text/html"));
        assertTrue(entryPoint.matchContentType("application/json"));
    }

    @Test //
    public void formData() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider2()).addEntryPoint(EntryPoint2.class).build();
        EntryPointScanner scanner = new EntryPointScanner(config);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(POST.class).get(0);
        assertTrue(entryPoint.matchContentType("multipart/form-data; boundary=----WebKitFormBoundaryqoNsVh2QtLJ19YqS"));
    }
}
