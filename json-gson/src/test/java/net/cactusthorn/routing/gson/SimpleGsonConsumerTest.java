package net.cactusthorn.routing.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RequestData;

public class SimpleGsonConsumerTest {

    @Test //
    public void consume() throws IOException {

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        RequestData requestData;
        try (InputStream is = SimpleGsonProducerTest.class.getClassLoader().getResourceAsStream("test.json");
                Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
                BufferedReader buf = new BufferedReader(reader)) {

            Mockito.when(request.getReader()).thenReturn(buf);
            requestData = new RequestData(request, null, true);
        }
        SimpleGsonConsumer consumer = new SimpleGsonConsumer();
        DataObject data = (DataObject) consumer.consume(DataObject.class, null, requestData);
        assertEquals("The Name \u00DF", data.getName());
        assertEquals(123, data.getValue());
    }

}
