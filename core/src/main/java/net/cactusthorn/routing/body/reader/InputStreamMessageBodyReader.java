package net.cactusthorn.routing.body.reader;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import net.cactusthorn.routing.body.BodyProcessor;

@Priority(BodyProcessor.PRIORITY_HIGHEST) //
public class InputStreamMessageBodyReader implements MessageBodyReader<InputStream> {

    @Override //
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (InputStream.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    @Override //
    public InputStream readFrom(Class<InputStream> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) {
        return entityStream;
    }
}
