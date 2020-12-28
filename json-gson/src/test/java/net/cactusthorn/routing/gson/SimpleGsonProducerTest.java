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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.gson.GsonBuilder;

public class SimpleGsonProducerTest {

    private HttpServletResponse response;
    private StringWriter stringWriter;
    private DataObject data;
    private String json;

    @BeforeEach //
    public void setUp() throws IOException {
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

        data = new DataObject("The Name \u00DF", 123);

        stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        response = Mockito.mock(HttpServletResponse.class);
        Mockito.when(response.getWriter()).thenReturn(writer);
    }

    @Test //
    public void produce() throws IOException {
        SimpleGsonProducer producer = new SimpleGsonProducer();

        producer.produce(data, null, null, null, response);

        assertEquals(json, stringWriter.toString());
    }

    @Test //
    public void produceWithCustomGson() throws IOException {
        SimpleGsonProducer producer = new SimpleGsonProducer(new GsonBuilder().create());

        producer.produce(data, null, null, null, response);

        assertEquals(json, stringWriter.toString());
    }

    @Test //
    public void produceNullData() throws IOException {

        Mockito.when(response.getStatus()).thenReturn(HttpServletResponse.SC_OK);

        SimpleGsonProducer producer = new SimpleGsonProducer();
        producer.produce(null, null, null, null, response);

        ArgumentCaptor<Integer> code = ArgumentCaptor.forClass(Integer.class);

        Mockito.verify(response).setStatus(code.capture());

        assertEquals(HttpServletResponse.SC_NO_CONTENT, code.getValue());
    }
}
