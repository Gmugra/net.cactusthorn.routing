package net.cactusthorn.routing.body.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.convert.ConvertersHolder;

@Priority(BodyReader.LOWEST_PRIORITY) //
public class WildcardMessageBodyReader implements InitializableMessageBodyReader<Object> {

    private ConvertersHolder convertersHolder;

    @Override //
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        this.convertersHolder = routingConfig.convertersHolder();
    }

    @Override //
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (InputStream.class.isAssignableFrom(type)) {
            return true;
        }
        if (Reader.class.isAssignableFrom(type)) {
            return true;
        }
        return convertersHolder.findConverter(type).isPresent();
    }

    @Override //
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        try {
            if (InputStream.class.isAssignableFrom(type)) {
                return entityStream;
            }
            String charset = mediaType.getParameters().get(MediaType.CHARSET_PARAMETER);
            if (Reader.class.isAssignableFrom(type)) {
                return new InputStreamReader(entityStream, charset);
            }
            return convertersHolder.findConverter(type).get().convert(type, streamToString(entityStream, charset));
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private String streamToString(InputStream inputStream, String charset) throws IOException {
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
