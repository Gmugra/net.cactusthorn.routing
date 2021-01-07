package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

        @PUT @Consumes({"text/aaa","xxx/ddd"}) //
        public void multi() {
        }
    }

    public static class EntryPoint1Provider1 implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    @Path("/") @Consumes({"text/*","sss/www"}) //
    public static class EntryPoint2 {

        @GET @Consumes("application/json,turbo/mega") //
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

    @ParameterizedTest @MethodSource("provideArguments") //
    public void testIt(ComponentProvider provider, Class<?> entryPointClass, Object key, String match ) {
        RoutingConfig config = RoutingConfig.builder(provider).addEntryPoint(entryPointClass).build();
        EntryPointScanner scanner = new EntryPointScanner(config);
        Map<Class<? extends Annotation>, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(key).get(0);
        assertTrue(entryPoint.matchContentType(match));
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of(new EntryPoint1Provider1(), EntryPoint1.class, GET.class, "aaa/vvvv"),
            Arguments.of(new EntryPoint1Provider1(), EntryPoint1.class, POST.class, "text/html"),
            Arguments.of(new EntryPoint1Provider1(), EntryPoint1.class, PUT.class, "text/aaa"),
            Arguments.of(new EntryPoint1Provider1(), EntryPoint1.class, PUT.class, "xxx/ddd"),
            Arguments.of(new EntryPoint1Provider2(), EntryPoint2.class, PUT.class, "text/html"),
            Arguments.of(new EntryPoint1Provider2(), EntryPoint2.class, PUT.class, "sss/www"),
            Arguments.of(new EntryPoint1Provider2(), EntryPoint2.class, GET.class, "application/json"),
            Arguments.of(new EntryPoint1Provider2(), EntryPoint2.class, GET.class, "turbo/mega"),
            Arguments.of(new EntryPoint1Provider2(), EntryPoint2.class, POST.class,
                    "multipart/form-data; boundary=----WebKitFormBoundaryqoNsVh2QtLJ19YqS"));
        // @formatter:on
    }
}
