package net.cactusthorn.routing.resource;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.annotation.Template;
import net.cactusthorn.routing.resource.ResourceScanner.Resource;
import net.cactusthorn.routing.uri.PathTemplate;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;
import net.cactusthorn.routing.util.Headers;

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
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider1()).addResource(EntryPoint1.class).build();
        ResourceScanner f = new ResourceScanner(config);
        Map<String, List<Resource>> entryPoints = f.scan();
        List<Resource> gets = entryPoints.get(HttpMethod.GET);

        for (Resource entryPoint : gets) {
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
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider2()).addResource(EntryPoint2.class).build();
        ResourceScanner f = new ResourceScanner(config);
        Map<String, List<Resource>> entryPoints = f.scan();
        List<Resource> gets = entryPoints.get(HttpMethod.GET);

        for (Resource entryPoint : gets) {
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
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider3()).addResource(EntryPoint3.class).build();
        ResourceScanner f = new ResourceScanner(config);
        Map<String, List<Resource>> entryPoints = f.scan();
        Resource entryPoint = entryPoints.get(HttpMethod.GET).get(0);

        assertTrue(entryPoint.match("/"));

        String header = MediaType.APPLICATION_JSON + ", " + MediaType.TEXT_PLAIN; 
        List<MediaType> accept = Headers.parseAccept(header);
        assertTrue(entryPoint.matchAccept(accept).isPresent());

        header = MediaType.APPLICATION_JSON; 
        accept = Headers.parseAccept(header);
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
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider4()).addResource(EntryPoint4.class).build();
        ResourceScanner f = new ResourceScanner(config);
        Map<String, List<Resource>> entryPoints = f.scan();
        Resource entryPoint = entryPoints.get(HttpMethod.GET).get(0);
        PathTemplate.PathValues values = entryPoint.parse("/api/");

        assertEquals(PathValues.EMPTY, values);
        assertTrue(entryPoint.match("/api/"));

        String header = MediaType.WILDCARD;
        List<MediaType> accept = Headers.parseAccept(header);
        assertTrue(entryPoint.matchAccept(accept).isPresent());
    }

    @Test //
    public void wow() {
        RoutingConfig config = RoutingConfig.builder(new EntryPoint1Provider4()).addResource(EntryPoint4.class).build();
        ResourceScanner f = new ResourceScanner(config);
        Map<String, List<Resource>> entryPoints = f.scan();
        Resource entryPoint = entryPoints.get(HttpMethod.HEAD).get(0);
        assertEquals("wow.html", entryPoint.template());
    }
}
