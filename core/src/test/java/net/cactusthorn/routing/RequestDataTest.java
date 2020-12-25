package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class RequestDataTest {

    private HttpServletRequest request;

    @BeforeEach //
    void mock() throws IOException {

        Map<String, String[]> parameterMap = new HashMap<>();
        parameterMap.put("test", new String[] { "PARAM1" });

        request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getParameterMap()).thenReturn(parameterMap);
    }

    @Test //
    public void ioexception() throws IOException {
        Mockito.when(request.getReader()).thenThrow(IOException.class);
        assertThrows(RoutingException.class,() -> new RequestData(request, null, 512));
    }

    @Test //
    public void reader() throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader("THE BODY"));
        Mockito.when(request.getReader()).thenReturn(reader);

        RequestData data = new RequestData(request, null, 512);

        assertEquals("THE BODY", data.requestBody().toString());
    }

    @Test //
    public void withoutBody() throws IOException {
        RequestData data = new RequestData(null);

        assertNull(data.requestBody());
    }
}
