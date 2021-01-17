package net.cactusthorn.routing.gson;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;

public class SimpleGsonBodyWriterTest {

    private ByteArrayOutputStream outputStream;
    private DataObject data;
    private String json;

    @BeforeEach //
    public void setUp() throws IOException {
        try (InputStream is = SimpleGsonBodyWriterTest.class.getClassLoader().getResourceAsStream("test.json");
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
        outputStream = new ByteArrayOutputStream();
    }

    @Test //
    public void produce() throws IOException {
        SimpleGsonBodyWriter<DataObject> writer = new SimpleGsonBodyWriter<>();
        writer.writeTo(data, DataObject.class, null, null, MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-8"), new MultivaluedHashMap<>(),
                outputStream);
        assertEquals(json, new String(outputStream.toByteArray(), "UTF-8"));
    }

    @Test //
    public void produceWithCustomGson() throws IOException {
        SimpleGsonBodyWriter<DataObject> writer = new SimpleGsonBodyWriter<>(new GsonBuilder().create());
        writer.writeTo(data, DataObject.class, null, null, MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-8"), new MultivaluedHashMap<>(),
                outputStream);
        assertEquals(json, new String(outputStream.toByteArray(), "UTF-8"));
    }

    @Test public void isWriteable() {
        SimpleGsonBodyWriter<DataObject> writer = new SimpleGsonBodyWriter<>(new GsonBuilder().create());
        assertFalse(writer.isWriteable(String.class, null, null, null));
        assertFalse(writer.isWriteable(DataObject.class, null, null, MediaType.TEXT_PLAIN_TYPE));
        assertTrue(writer.isWriteable(DataObject.class, null, null, MediaType.APPLICATION_JSON_TYPE));
    }
}
