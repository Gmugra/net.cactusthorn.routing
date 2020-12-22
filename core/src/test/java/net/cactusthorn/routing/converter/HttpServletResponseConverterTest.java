package net.cactusthorn.routing.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class HttpServletResponseConverterTest {

    @Test //
    public void test() {

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getCharacterEncoding()).thenReturn("UTF-8");

        HttpServletResponseConverter c = new HttpServletResponseConverter();
        HttpServletResponse result = c.convert(null, response, null, null);
        assertEquals("UTF-8", result.getCharacterEncoding());
    }
}
