package net.cactusthorn.routing.body.writer;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.RoutingConfig;

public class BodyWriterTest {

    private static final MessageBodyWriter<Object> WRITER = new InitializableMessageBodyWriter<Object>() {
        @Override public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return true;
        }

        @Override public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            return;
        }
    };

    @Priority(3000) private static final class InitializableWriter implements InitializableMessageBodyWriter<Object> {
        @Override public void init(ServletContext servletContext, RoutingConfig routingConfig) {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return false;
        }

        @Override public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            return;
        }
    };

    private static final InitializableWriter INITIALIZABLE_WRITER = new InitializableWriter();

    @Test //
    public void checkNull() {
        assertThrows(IllegalArgumentException.class, () -> new BodyWriter(null, new WildcardMessageBodyWriter()));
        assertThrows(IllegalArgumentException.class, () -> new BodyWriter(MediaType.WILDCARD_TYPE, null));
    }

    @Test //
    public void simple() {
        BodyWriter bodyWriter = new BodyWriter(MediaType.WILDCARD_TYPE, INITIALIZABLE_WRITER);
        assertEquals(3000, bodyWriter.priority());
        assertEquals(MediaType.WILDCARD_TYPE, bodyWriter.mediaType());
        assertTrue(bodyWriter.initializable());
        assertNotNull(bodyWriter.messageBodyWriter());
    }

    @Test //
    public void initCall() {
        BodyWriter bodyWriter = new BodyWriter(MediaType.WILDCARD_TYPE, INITIALIZABLE_WRITER);
        assertThrows(UnsupportedOperationException.class, () -> bodyWriter.init(null, null));
    }

    @Test //
    public void initNoCall() {
        BodyWriter bodyWriter = new BodyWriter(MediaType.WILDCARD_TYPE, WRITER);
        assertTrue(bodyWriter.isProcessable(null, null, null, null));
        bodyWriter.init(null, null);
    }
}
