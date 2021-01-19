package net.cactusthorn.routing.body.reader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.body.Initializable;
import net.cactusthorn.routing.body.BodyProcessor;

public class BodyReader extends BodyProcessor {

    private MessageBodyReader<?> messageBodyReader;

    public BodyReader(MessageBodyReader<?> messageBodyReader) {
        super(messageBodyReader.getClass());
        this.messageBodyReader = messageBodyReader;
    }

    public MessageBodyReader<?> messageBodyReader() {
        return messageBodyReader;
    }

    @Override //
    protected String[] getMediaTypeAnnotationValue(Class<?> clazz) {
        Consumes annotation = clazz.getAnnotation(Consumes.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    @Override //
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        if (initializable()) {
            ((Initializable) messageBodyReader).init(servletContext, routingConfig);
        }
    }

    @Override //
    public boolean isProcessable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return super.isProcessable(type, genericType, annotations, mediaType)
                && messageBodyReader.isReadable(type, genericType, annotations, mediaType);
    }
}
