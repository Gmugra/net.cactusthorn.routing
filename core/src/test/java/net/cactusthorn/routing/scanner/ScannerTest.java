package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.EntryPointScanner;
import net.cactusthorn.routing.PathTemplate;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.Http;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.annotation.Template;

import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    
    protected Method findMethod(Class<?> clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
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
        Map<String, List<EntryPoint>> entryPoints = f.scan();
        List<EntryPoint> gets = entryPoints.get(HttpMethod.GET);

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
        Map<String, List<EntryPoint>> entryPoints = f.scan();
        List<EntryPoint> gets = entryPoints.get(HttpMethod.GET);

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
        Map<String, List<EntryPoint>> entryPoints = f.scan();
        EntryPoint entryPoint = entryPoints.get(HttpMethod.GET).get(0);

        assertTrue(entryPoint.match("/"));

        List<String> header = new ArrayList<>();
        header.add(MediaType.APPLICATION_JSON);
        header.add(MediaType.TEXT_PLAIN);
        List<MediaType> accept = Http.parseAccept(Collections.enumeration(header));
        assertTrue(entryPoint.matchAccept(accept).isPresent());

        header.clear();
        header.add(MediaType.APPLICATION_JSON);
        accept = Http.parseAccept(Collections.enumeration(header));
        assertFalse(entryPoint.matchAccept(accept).isPresent());
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
        Map<String, List<EntryPoint>> entryPoints = f.scan();
        EntryPoint entryPoint = entryPoints.get(HttpMethod.GET).get(0);
        PathTemplate.PathValues values = entryPoint.parse("/api/");

        assertEquals(PathValues.EMPTY, values);
        assertTrue(entryPoint.match("/api/"));

        List<String> header = new ArrayList<>();
        header.add(MediaType.WILDCARD);
        List<MediaType> accept = Http.parseAccept(Collections.enumeration(header));
        assertTrue(entryPoint.matchAccept(accept).isPresent());
    }

    @Test //
    public void wow() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider4()).addEntryPoint(EntryPoint4.class).build();
        EntryPointScanner f = new EntryPointScanner(config);
        Map<String, List<EntryPoint>> entryPoints = f.scan();
        EntryPoint entryPoint = entryPoints.get(HttpMethod.HEAD).get(0);
        assertEquals("wow.html", entryPoint.template());
    }
}
