package net.cactusthorn.routing.bodyreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import net.cactusthorn.routing.convert.ConvertersHolder;

public class TextPlainBodyReader implements MessageBodyReader<Object> {

    private ConvertersHolder convertersHolder;

    public TextPlainBodyReader(ConvertersHolder convertersHolder) {
        this.convertersHolder = convertersHolder;
    }

    @Override //
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (!MediaType.TEXT_PLAIN_TYPE.isCompatible(mediaType)) {
            return false;
        }
        if (type.isAssignableFrom(InputStream.class)) {
            return true;
        }
        return convertersHolder.findConverter(type).isPresent();
    }

    @Override //
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        if (type.isAssignableFrom(InputStream.class)) {
            return entityStream;
        }
        try {
            return convertersHolder.findConverter(type).get().convert(type, streamToString(httpHeaders, entityStream));
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private String streamToString(MultivaluedMap<String, String> httpHeaders, InputStream inputStream) throws IOException {
        String charset = MediaType.valueOf(httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE)).getParameters().get(MediaType.CHARSET_PARAMETER);
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        return textBuilder.toString();
    }
}
