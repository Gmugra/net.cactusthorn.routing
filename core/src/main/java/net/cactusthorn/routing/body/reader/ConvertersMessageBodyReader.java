package net.cactusthorn.routing.body.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.body.BodyProcessor;
import net.cactusthorn.routing.convert.ConvertersHolder;

@Priority(BodyProcessor.LOWEST_PRIORITY) //
public class ConvertersMessageBodyReader extends MessageBodyReaderAncestor<Object> {

    private ConvertersHolder convertersHolder;

    @Override //
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        super.init(servletContext, routingConfig);
        this.convertersHolder = routingConfig.convertersHolder();
    }

    @Override //
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return convertersHolder.findConverter(type, genericType, annotations).isPresent();
    }

    @Override //
    public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        try {
            return convertersHolder.findConverter(type, genericType, annotations).get().convert(type, genericType, annotations,
                    streamToString(entityStream, mediaType));
        } catch (IOException e) {
            throw e;
        } catch (Throwable e) {
            throw new ProcessingException(e);
        }
    }
}
