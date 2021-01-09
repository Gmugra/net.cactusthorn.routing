package net.cactusthorn.routing.scanner;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import net.cactusthorn.routing.*;
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
    public void testIt(ComponentProvider provider, Class<?> entryPointClass, String httpMethod, String match ) {
        RoutingConfig config = RoutingConfig.builder(provider).addEntryPoint(entryPointClass).build();
        EntryPointScanner scanner = new EntryPointScanner(config);
        Map<String, List<EntryPoint>> entryPoints = scanner.scan();
        EntryPoint entryPoint = entryPoints.get(httpMethod).get(0);
        assertTrue(entryPoint.matchContentType(match));
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of(new EntryPoint1Provider1(), EntryPoint1.class, HttpMethod.GET, "aaa/vvvv"),
            Arguments.of(new EntryPoint1Provider1(), EntryPoint1.class, HttpMethod.POST, "text/html"),
            Arguments.of(new EntryPoint1Provider1(), EntryPoint1.class, HttpMethod.PUT, "text/aaa"),
            Arguments.of(new EntryPoint1Provider1(), EntryPoint1.class, HttpMethod.PUT, "xxx/ddd"),
            Arguments.of(new EntryPoint1Provider2(), EntryPoint2.class, HttpMethod.PUT, "text/html"),
            Arguments.of(new EntryPoint1Provider2(), EntryPoint2.class, HttpMethod.PUT, "sss/www"),
            Arguments.of(new EntryPoint1Provider2(), EntryPoint2.class, HttpMethod.GET, "application/json"),
            Arguments.of(new EntryPoint1Provider2(), EntryPoint2.class, HttpMethod.GET, "turbo/mega"),
            Arguments.of(new EntryPoint1Provider2(), EntryPoint2.class, HttpMethod.POST,
                    "multipart/form-data; boundary=----WebKitFormBoundaryqoNsVh2QtLJ19YqS"));
        // @formatter:on
    }
}
