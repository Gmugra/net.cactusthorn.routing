package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.FormPart;

public class FormPartParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void simple(@FormPart("val") Part value) {
        }

        public void byName(@FormPart Part val) {
        }

        public void wrongType(@FormPart("val") String value) {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    public static class TestPart implements Part {

        private String name;

        public TestPart(String name) {
            this.name = name;
        }

        @Override public InputStream getInputStream() throws IOException {
            return null;
        }

        @Override public String getContentType() {
            return null;
        }

        @Override public String getName() {
            return name;
        }

        @Override public String getSubmittedFileName() {
            return null;
        }

        @Override public long getSize() {
            return 0;
        }

        @Override public void write(String fileName) throws IOException {
        }

        @Override public void delete() throws IOException {
        }

        @Override public String getHeader(String name) {
            return null;
        }

        @Override public Collection<String> getHeaders(String name) {
            return null;
        }

        @Override public Collection<String> getHeaderNames() {
            return null;
        }
    }

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    @Test //
    public void wrongType() {
        assertThrows(RoutingInitializationException.class, () -> parameterInfo(EntryPoint1.class, "wrongType", CONFIG));
    }

    @ParameterizedTest @MethodSource("provideArguments") //
    public void getParts(String methodName, List<Part> requestParts, boolean expectedNull) throws Exception {
        MethodParameter mp = parameterInfo(EntryPoint1.class, methodName, CONFIG);

        Mockito.when(request.getParts()).thenReturn(requestParts);
        Part part = (Part) mp.findValue(request, null, null, null);
        if (expectedNull) {
            assertNull(part);
        } else {
            assertEquals(requestParts.get(0).getName(), part.getName());
        }
    }

    private static Stream<Arguments> provideArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("simple", Arrays.asList(new TestPart[] {new TestPart("other")}), true),
            Arguments.of("simple", null, true),
            Arguments.of("simple", Arrays.asList(new TestPart[] {new TestPart("val")}), false),
            Arguments.of("byName", Arrays.asList(new TestPart[] {new TestPart("val")}), false));
        // @formatter:on
    }
}
