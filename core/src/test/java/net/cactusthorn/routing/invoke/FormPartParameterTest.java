package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.FormPart;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class FormPartParameterTest {

    static final ConvertersHolder HOLDER = new ConvertersHolder();

    public static class EntryPoint1 {

        public void simple(@FormPart("val") Part value) {
        }

        public void wrongType(@FormPart("val") String value) {
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

    HttpServletRequest request;

    @BeforeEach //
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test //
    public void simple() throws Exception {
        Method m = findMethod("simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        List<Part> parts = new ArrayList<>();
        parts.add(new TestPart("val"));

        Mockito.when(request.getParts()).thenReturn(parts);
        Part part = (Part) mp.findValue(request, null, null, null);
        assertEquals("val", part.getName());
    }

    @Test //
    public void notfound() throws Exception {
        Method m = findMethod("simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getParts()).thenReturn(null);
        Part part = (Part) mp.findValue(request, null, null, null);
        assertNull(part);
    }

    @Test //
    public void notfound2() throws Exception {
        Method m = findMethod("simple");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        List<Part> parts = new ArrayList<>();
        parts.add(new TestPart("other"));

        Mockito.when(request.getParts()).thenReturn(parts);
        Part part = (Part) mp.findValue(request, null, null, null);
        assertNull(part);
    }

    @Test //
    public void wrongType() throws ConverterException {
        Method m = findMethod("wrongType");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    private Method findMethod(String methodName) {
        for (Method method : EntryPoint1.class.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }
}
