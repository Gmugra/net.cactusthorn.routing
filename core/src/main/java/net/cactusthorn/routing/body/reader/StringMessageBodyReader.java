package net.cactusthorn.routing.body.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import net.cactusthorn.routing.body.BodyProcessor;

@Priority(BodyProcessor.PRIORITY_HIGHEST) //
public class StringMessageBodyReader extends MessageBodyReaderAncestor<String> {

    @Override //
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == String.class;
    }

    @Override //
    public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        try {
            return streamToString(entityStream, mediaType);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new ProcessingException(e);
        }
    }

}
