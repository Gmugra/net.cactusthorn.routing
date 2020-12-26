package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.EntryPointScanner;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.RoutingConfig.ConfigProperty;
import net.cactusthorn.routing.annotation.GET;
import net.cactusthorn.routing.annotation.Path;
import net.cactusthorn.routing.convert.ConvertersHolder;

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
        EntryPointScanner f = new EntryPointScanner(Arrays.asList(EntryPoint1.class), new EntryPoint1Provider(), HOLDER, PROPERTIES);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        List<EntryPoint> gets = entryPoints.get(GET.class);

        assertEquals("/api/dddd/sssss/", gets.get(0).pathTemplatePattern());
        assertEquals("/api/(\\d{3})/dddd/", gets.get(1).pathTemplatePattern());
        assertEquals("/api/([^/]+)/dddd/", gets.get(2).pathTemplatePattern());
        assertEquals("/api/dddd/", gets.get(3).pathTemplatePattern());
    }
}
