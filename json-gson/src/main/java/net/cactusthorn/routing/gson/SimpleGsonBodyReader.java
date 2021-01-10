package net.cactusthorn.routing.gson;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SimpleGsonBodyReader<T> implements MessageBodyReader<T> {

    private Gson gson;

    public SimpleGsonBodyReader(Gson gson) {
        this.gson = gson;
    }

    public SimpleGsonBodyReader() {
        gson = new GsonBuilder().create();
    }

    @Override //
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType);
    }

    @SuppressWarnings("unchecked") //
    public T readFrom(Class<T> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {

        String charset = MediaType.valueOf(httpHeaders.getFirst(HttpHeaders.CONTENT_TYPE)).getParameters().get(MediaType.CHARSET_PARAMETER);

        if (type.isAssignableFrom(InputStream.class)) {
            return (T) entityStream;
        }
        try (Reader reader = new InputStreamReader(entityStream, charset)) {
            return gson.fromJson(reader, type);
        }
    }
}
