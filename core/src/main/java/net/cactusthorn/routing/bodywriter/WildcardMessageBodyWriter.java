package net.cactusthorn.routing.bodywriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

public class WildcardMessageBodyWriter implements MessageBodyWriter<Object> {

    @Override //
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override //
    public void writeTo(Object entity, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);

        if (mediaType.isWildcardType() && mediaType.isWildcardSubtype()) {
            httpHeaders.addFirst(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_TYPE.withCharset(charset));
        } else {
            httpHeaders.addFirst(HttpHeaders.CONTENT_TYPE, mediaType);
        }

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(entityStream, charset))) {
            writer.write(String.valueOf(entity));
        }
    }
}
