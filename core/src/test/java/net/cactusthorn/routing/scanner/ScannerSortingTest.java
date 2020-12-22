package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.EntryPointScanner;
import net.cactusthorn.routing.EntryPointScanner.EntryPoint;
import net.cactusthorn.routing.annotation.GET;
import net.cactusthorn.routing.annotation.Path;
import net.cactusthorn.routing.converter.ConvertersHolder;

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

    private static final ConvertersHolder HOLDER = new ConvertersHolder();

    @Test //
    public void entryPoint1() {
        EntryPointScanner f = new EntryPointScanner(Arrays.asList(EntryPoint1.class), new EntryPoint1Provider(), HOLDER);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = f.scan();
        List<EntryPoint> gets = entryPoints.get(GET.class);
        assertEquals("/api/dddd/sssss", gets.get(0).template());
        assertEquals("/api/(\\d{3})/dddd", gets.get(1).template());
        assertEquals("/api/([^/]+)/dddd", gets.get(2).template());
        assertEquals("/api/dddd", gets.get(3).template());
    }
}
