package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.EntryPointScanner;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.annotation.GET;
import net.cactusthorn.routing.annotation.Path;

public class ScannerCollectionTest {

    @Path("/api") //
    public static class EntryPoint1 {

        @GET @Path("dddd") //
        public void m1() {
        }
    }

    public static class EntryPoint2 {

        @GET @Path("api/dddd") //
        public void m2() {
        }
    }

    public static class EntryPoint3 {

        @GET @Path("/api/dddd/") //
        public void m3() {
        }
    }

    @Path("api") //
    public static class EntryPoint4 {

        @GET @Path("/dddd/") //
        public void m4() {
        }
    }

    public static class EntryPointProvider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            if (clazz == EntryPoint1.class) {
                return new EntryPoint1();
            }
            if (clazz == EntryPoint2.class) {
                return new EntryPoint2();
            }
            if (clazz == EntryPoint3.class) {
                return new EntryPoint3();
            }
            if (clazz == EntryPoint4.class) {
                return new EntryPoint4();
            }
            return null;
        }
    }

    @Test //
    public void entryPoint() {
        List<Class<?>> classes = Arrays.asList(EntryPoint1.class, EntryPoint2.class, EntryPoint3.class, EntryPoint4.class);
        RoutingConfig config = RoutingConfig.builder(new EntryPointProvider()).addEntryPoint(classes).build();
        EntryPointScanner f = new EntryPointScanner(config);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        List<EntryPoint> gets = entryPoints.get(GET.class);
        assertEquals(4, gets.size());
        for (EntryPoint entryPoint : gets) {
            assertTrue(entryPoint.match("/api/dddd/"));
        }
    }
}
