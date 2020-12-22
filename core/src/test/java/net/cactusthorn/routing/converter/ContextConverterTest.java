package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.*;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.annotation.Context;

public class ContextConverterTest {

    public static class EntryPoint1 {
        public void m1(@Context HttpServletRequest p) {
        }

        public void m0(@Context java.util.Date p) {
        }

        public void m2(@Context HttpSession p) {
        }

        public void m3(@Context ServletContext p) {
        }

        public void m4(@Context HttpServletResponse p) {
        }
    }

    private static ConvertersHolder holder;

    @BeforeAll //
    static void setUp() {
        holder = new ConvertersHolder();
    }

    @Test //
    public void request() throws Exception {
        Parameter parameter = findParameter("m1");
        Converter<?> converter = holder.findConverter(parameter);
        assertEquals(HttpServletRequestConverter.class, converter.getClass());
    }

    @Test //
    public void unknown() throws Exception {
        Parameter parameter = findParameter("m0");
        Converter<?> converter = holder.findConverter(parameter);
        assertEquals(NullConverter.class, converter.getClass());
    }

    @Test //
    public void session() throws Exception {
        Parameter parameter = findParameter("m2");
        Converter<?> converter = holder.findConverter(parameter);
        assertEquals(HttpSessionConverter.class, converter.getClass());
    }

    private Parameter findParameter(String methodName) {
        for (Method method : EntryPoint1.class.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method.getParameters()[0];
            }
        }
        return null;
    }
}
