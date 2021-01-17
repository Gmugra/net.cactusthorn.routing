package net.cactusthorn.routing.gson;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.GsonBuilder;

public class SimpleGsonBodyReaderTest {

    InputStream is;

    @BeforeEach void setUp() {
        is = SimpleGsonBodyWriterTest.class.getClassLoader().getResourceAsStream("test.json");
    }

    @Test @SuppressWarnings({ "rawtypes", "unchecked" }) //
    public void read() throws IOException {
        SimpleGsonBodyReader bodyReader = new SimpleGsonBodyReader();
        DataObject data = (DataObject) bodyReader.readFrom(DataObject.class, null, null,
                MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-8"), null, is);
        assertEquals("The Name \u00DF", data.getName());
        assertEquals(123, data.getValue());
    }

    @Test @SuppressWarnings({ "rawtypes", "unchecked" }) //
    public void readWithCustomGson() throws IOException {
        SimpleGsonBodyReader bodyReader = new SimpleGsonBodyReader(new GsonBuilder().create());
        DataObject data = (DataObject) bodyReader.readFrom(DataObject.class, null, null,
                MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-8"), null, is);
        assertEquals("The Name \u00DF", data.getName());
        assertEquals(123, data.getValue());
    }

    @Test @SuppressWarnings({ "rawtypes", "unchecked" }) //
    public void isReadable() {
        SimpleGsonBodyReader bodyReader = new SimpleGsonBodyReader(new GsonBuilder().create());
        assertTrue(bodyReader.isReadable(String.class, null, null, MediaType.APPLICATION_JSON_TYPE));
        assertFalse(bodyReader.isReadable(String.class, null, null, MediaType.TEXT_PLAIN_TYPE));
        assertFalse(bodyReader.isReadable(InputStream.class, null, null, MediaType.APPLICATION_JSON_TYPE));
        assertFalse(bodyReader.isReadable(Reader.class, null, null, MediaType.APPLICATION_JSON_TYPE));
    }
}
