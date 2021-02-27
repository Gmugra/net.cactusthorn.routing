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
import javax.ws.rs.Consumes;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.util.Prioritised;

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

    @Priority(3000) @Consumes({ "aa/bb;q=0.5","cc/dd,zz/ff" }) //
    private static final class InitializableReader implements InitializableMessageBodyReader<Object> {
        @Override public void init(ServletContext servletContext, RoutingConfig routingConfig) {
            throw new UnsupportedOperationException();
        }

        @Override public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return true;
        }

        @Override public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
            return null;
        }
    };

    private static final InitializableReader INITIALIZABLE_READER = new InitializableReader();

    @Test //
    public void simple() {
        BodyReader bodyReader = new BodyReader(new ConvertersMessageBodyReader());
        assertEquals(BodyReader.LOWEST_PRIORITY, bodyReader.priority());
        assertEquals("9999 :: net.cactusthorn.routing.body.reader.ConvertersMessageBodyReader", bodyReader.toString());
        assertTrue(bodyReader.initializable());
    }

    @Test //
    public void _default() {
        BodyReader bodyReader = new BodyReader(READER);
        assertEquals(Priorities.USER, bodyReader.priority());
        assertFalse(bodyReader.initializable());
    }

    @Test //
    public void ignoreParameters() {
        BodyReader bodyReader = new BodyReader(INITIALIZABLE_READER);
        assertFalse(bodyReader.isProcessable(null, null, null, MediaType.APPLICATION_JSON_PATCH_JSON_TYPE));
        assertTrue(bodyReader.isProcessable(null, null, null, MediaType.valueOf("aa/bb")));
    }

    @Test //
    public void initCall() {
        BodyReader bodyReader = new BodyReader(INITIALIZABLE_READER);
        assertThrows(UnsupportedOperationException.class, () -> bodyReader.init(null, null));
    }

    @Test //
    public void initNoCall() {
        BodyReader bodyReader = new BodyReader(READER);
        bodyReader.init(null, null);
    }

    @Test //
    public void comparator() {
        List<BodyReader> bodyReaders = new ArrayList<>();
        bodyReaders.add(new BodyReader(new ConvertersMessageBodyReader()));
        bodyReaders.add(null);
        bodyReaders.add(new BodyReader(READER));
        bodyReaders.add(new BodyReader(INITIALIZABLE_READER));
        bodyReaders.add(null);

        Collections.sort(bodyReaders, Prioritised.PRIORITY_COMPARATOR);

        assertEquals("3000 :: net.cactusthorn.routing.body.reader.BodyReaderTest$InitializableReader",
                bodyReaders.get(0).toString());
        assertEquals("5000 :: net.cactusthorn.routing.body.reader.BodyReaderTest$1", bodyReaders.get(1).toString());
        assertEquals("9999 :: net.cactusthorn.routing.body.reader.ConvertersMessageBodyReader", bodyReaders.get(2).toString());
        assertNull(bodyReaders.get(3));
        assertNull(bodyReaders.get(4));
    }
}
