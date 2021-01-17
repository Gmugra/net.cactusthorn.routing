package net.cactusthorn.routing.gson;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

public class SimpleGsonBodyWriter<T> implements MessageBodyWriter<T> {

    private Gson gson;

    public SimpleGsonBodyWriter(Gson gson) {
        this.gson = gson;
    }

    public SimpleGsonBodyWriter() {
        gson = new GsonBuilder().create();
    }

    @Override //
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (String.class.isAssignableFrom(type)) {
            return false;
        }
        return MediaType.APPLICATION_JSON_TYPE.isCompatible(mediaType);
    }

    @Override //
    public void writeTo(T object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

        httpHeaders.addFirst("X-Routing-BodyWriter", SimpleGsonBodyWriter.class.getSimpleName());
        String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
        try (JsonWriter writer = new JsonWriter(new OutputStreamWriter(entityStream, charset))) {
            gson.toJson(object, type, writer);
        }
    }
}
