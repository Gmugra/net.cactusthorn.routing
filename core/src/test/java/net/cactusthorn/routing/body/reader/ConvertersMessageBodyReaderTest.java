package net.cactusthorn.routing.body.reader;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;

public class ConvertersMessageBodyReaderTest {

    public static class TestProvider implements ComponentProvider {
        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return null;
        }
    }

    @Test //
    public void isReadable() {
        RoutingConfig config = RoutingConfig.builder(new TestProvider()).build();
        ConvertersMessageBodyReader bodyReader = new ConvertersMessageBodyReader();
        bodyReader.init(null, config);
        assertTrue(bodyReader.isReadable(Integer.class, null, null, MediaType.APPLICATION_OCTET_STREAM_TYPE));
        assertFalse(bodyReader.isReadable(InputStream.class, null, null, MediaType.APPLICATION_OCTET_STREAM_TYPE));
    }

    @Test @SuppressWarnings({ "rawtypes", "unchecked" }) //
    public void exception() {
        RoutingConfig config = RoutingConfig.builder(new TestProvider()).build();
        ByteArrayInputStream is = new ByteArrayInputStream(new byte[0]);
        InitializableMessageBodyReader reader = new ConvertersMessageBodyReader();
        reader.init(null, config);
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset("RRR-10");
        assertThrows(IOException.class, () -> reader.readFrom(Integer.class, null, null, mediaType, null, is));
    }

    @Test @SuppressWarnings({ "rawtypes", "unchecked" }) //
    public void ProcessingException() throws WebApplicationException, IOException {
        RoutingConfig config = RoutingConfig.builder(new TestProvider()).build();
        ByteArrayInputStream is = new ByteArrayInputStream("abc".getBytes());
        InitializableMessageBodyReader reader = new ConvertersMessageBodyReader();
        reader.init(null, config);
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE.withCharset("UTF-8");
        assertThrows(ProcessingException.class, () -> reader.readFrom(Integer.class, null, null, mediaType, null, is));
    }
}
