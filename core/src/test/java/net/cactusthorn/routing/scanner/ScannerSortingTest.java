package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.EntryPointScanner;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.scanner.ScannerTest.EntryPoint1Provider1;

public class ScannerSortingTest {

    @Path("/api") //
    public static class EntryPoint1 {

        @GET @Path("dddd") //
        public void m1() {
        }

        @GET @Path("/{id}/dddd") //
        public void m2() {
        }

        @GET @Path("dddd/sssss") public void m3() {
        }

        @GET @Path("/{ id : \\d{3} }/dddd") public void m4() {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

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

        assertEquals("/api/dddd/sssss/", gets.get(0).pathTemplatePattern());
        assertEquals("/api/(\\d{3})/dddd/", gets.get(1).pathTemplatePattern());
        assertEquals("/api/([^/]+)/dddd/", gets.get(2).pathTemplatePattern());
        assertEquals("/api/dddd/", gets.get(3).pathTemplatePattern());
    }
}
