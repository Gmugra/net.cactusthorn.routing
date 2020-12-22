package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.servlet.ServletContext;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ServletContextConverterTest {

    @Test //
    public void test() {

        ServletContext context = Mockito.mock(ServletContext.class);
        Mockito.when(context.getAttribute("abc")).thenReturn("xyz");

        ServletContextConverter c = new ServletContextConverter();
        ServletContext result = c.convert(null, null, context, null);
        assertEquals("xyz", result.getAttribute("abc"));
    }
}
