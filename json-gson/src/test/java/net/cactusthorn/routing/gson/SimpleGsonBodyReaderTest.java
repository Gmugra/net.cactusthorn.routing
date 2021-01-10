package net.cactusthorn.routing.gson;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;

public class SimpleGsonBodyReaderTest {

    InputStream is;
    MultivaluedMap<String, String> httpHeaders;

    @BeforeEach void setUp() {
        is = SimpleGsonProducerTest.class.getClassLoader().getResourceAsStream("test.json");
        httpHeaders = new MultivaluedHashMap<>();
        httpHeaders.putSingle(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-8").toString());
    }

    @Test @SuppressWarnings({ "rawtypes", "unchecked" }) //
    public void read() throws IOException {
        SimpleGsonBodyReader bodyReader = new SimpleGsonBodyReader();
        DataObject data = (DataObject) bodyReader.readFrom(DataObject.class, null, null, MediaType.APPLICATION_JSON_TYPE, httpHeaders, is);
        assertEquals("The Name \u00DF", data.getName());
        assertEquals(123, data.getValue());
    }

    @Test @SuppressWarnings({ "rawtypes", "unchecked" }) //
    public void readWithCustomGson() throws IOException {
        SimpleGsonBodyReader bodyReader = new SimpleGsonBodyReader(new GsonBuilder().create());
        DataObject data = (DataObject) bodyReader.readFrom(DataObject.class, null, null, MediaType.APPLICATION_JSON_TYPE, httpHeaders, is);
        assertEquals("The Name \u00DF", data.getName());
        assertEquals(123, data.getValue());
    }

    @Test @SuppressWarnings({ "rawtypes", "unchecked" }) //
    public void readStream() throws IOException {
        SimpleGsonBodyReader bodyReader = new SimpleGsonBodyReader(new GsonBuilder().create());
        InputStream result = (InputStream) bodyReader.readFrom(InputStream.class, null, null, MediaType.APPLICATION_JSON_TYPE, httpHeaders,
                is);
        assertNotNull(result);
    }

    @Test @SuppressWarnings({ "rawtypes", "unchecked" }) //
    public void isReadable() throws IOException {
        SimpleGsonBodyReader bodyReader = new SimpleGsonBodyReader(new GsonBuilder().create());
        assertTrue(bodyReader.isReadable(null, null, null, MediaType.APPLICATION_JSON_TYPE));
        assertFalse(bodyReader.isReadable(null, null, null, MediaType.TEXT_PLAIN_TYPE));
    }
}
