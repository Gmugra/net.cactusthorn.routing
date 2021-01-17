package net.cactusthorn.routing.body.reader;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.RoutingConfig;

public class BodyReaderTest {

    private static final MessageBodyReader<Object> READER = new MessageBodyReader<Object>() {
        @Override public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return false;
        }

        @Override public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            return null;
        }
    };

    @Priority(3000) private static final class InitializableReader implements InitializableMessageBodyReader<Object> {
        @Override public void init(ServletContext servletContext, RoutingConfig routingConfig) {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return false;
        }

        @Override public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            return null;
        }
    };

    private static final InitializableReader INITIALIZABLE_READER = new InitializableReader();

    @Test //
    public void checkNull() {
        assertThrows(IllegalArgumentException.class, () -> new BodyReader(null, new WildcardMessageBodyReader()));
        assertThrows(IllegalArgumentException.class, () -> new BodyReader(MediaType.WILDCARD_TYPE, null));
    }

    @Test //
    public void simple() {
        BodyReader bodyReader = new BodyReader(MediaType.WILDCARD_TYPE, new WildcardMessageBodyReader());
        assertEquals(BodyReader.LOWEST_PRIORITY, bodyReader.priority());
        assertEquals(MediaType.WILDCARD_TYPE, bodyReader.mediaType());
        assertTrue(bodyReader.initializable());
    }

    @Test //
    public void _default() {
        BodyReader bodyReader = new BodyReader(MediaType.APPLICATION_JSON_TYPE, READER);
        assertEquals(Priorities.USER, bodyReader.priority());
        assertEquals(MediaType.APPLICATION_JSON_TYPE, bodyReader.mediaType());
        assertFalse(bodyReader.initializable());
    }

    @Test //
    public void ignoreParameters() {
        BodyReader bodyReader = new BodyReader(MediaType.WILDCARD_TYPE.withCharset("UTF-8"), new WildcardMessageBodyReader());
        assertEquals("*/*", bodyReader.mediaType().toString());
    }

    @Test //
    public void initCall() {
        BodyReader bodyReader = new BodyReader(MediaType.WILDCARD_TYPE, INITIALIZABLE_READER);
        assertThrows(UnsupportedOperationException.class, () -> bodyReader.init(null, null));
    }

    @Test //
    public void initNoCall() {
        BodyReader bodyReader = new BodyReader(MediaType.WILDCARD_TYPE, READER);
        bodyReader.init(null, null);
    }

    @Test //
    public void comparator() {
        List<BodyReader> bodyReaders = new ArrayList<>();
        bodyReaders.add(new BodyReader(new MediaType("application", "*"), READER));
        bodyReaders.add(new BodyReader(new MediaType("*", "json"), READER));
        bodyReaders.add(new BodyReader(MediaType.WILDCARD_TYPE, new WildcardMessageBodyReader()));
        bodyReaders.add(null);
        bodyReaders.add(new BodyReader(MediaType.APPLICATION_JSON_TYPE, READER));
        bodyReaders.add(new BodyReader(MediaType.TEXT_HTML_TYPE, INITIALIZABLE_READER));
        bodyReaders.add(new BodyReader(MediaType.WILDCARD_TYPE, READER));
        bodyReaders.add(new BodyReader(new MediaType("test", "*"), READER));
        bodyReaders.add(new BodyReader(MediaType.TEXT_PLAIN_TYPE, READER));
        bodyReaders.add(null);

        Collections.sort(bodyReaders, BodyReader.COMPARATOR);

        assertEquals("text/html::3000", bodyReaders.get(0).toString());
        assertEquals("application/json::5000", bodyReaders.get(1).toString());
        assertEquals("text/plain::5000", bodyReaders.get(2).toString());
        assertEquals("application/*::5000", bodyReaders.get(3).toString());
        assertEquals("test/*::5000", bodyReaders.get(4).toString());
        assertEquals("*/json::5000", bodyReaders.get(5).toString());
        assertEquals("*/*::5000", bodyReaders.get(6).toString());
        assertEquals("*/*::9999", bodyReaders.get(7).toString());
        assertNull(bodyReaders.get(8));
        assertNull(bodyReaders.get(9));
    }
}
