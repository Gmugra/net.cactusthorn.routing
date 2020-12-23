package net.cactusthorn.routing.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class SimpleGsonProducerTest {

    @Test //
    public void produce() throws IOException {

        String json;
        try (InputStream is = SimpleGsonProducerTest.class.getClassLoader().getResourceAsStream("test.json");
                Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            char[] charBuffer = new char[1024];
            StringBuilder builder = new StringBuilder();
            int numCharsRead;
            while ((numCharsRead = reader.read(charBuffer, 0, charBuffer.length)) != -1) {
                builder.append(charBuffer, 0, numCharsRead);
            }
            json = builder.toString();
        }

        DataObject data = new DataObject("The Name \u00DF", 123);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getWriter()).thenReturn(writer);

        SimpleGsonProducer producer = new SimpleGsonProducer();

        producer.produce(data, null, null, response);

        assertEquals(json, stringWriter.toString());
    }
}
