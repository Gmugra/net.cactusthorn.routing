package net.cactusthorn.routing.body.writer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.body.Initializable;
import net.cactusthorn.routing.body.BodyProcessor;

public class BodyWriter extends BodyProcessor {

    private MessageBodyWriter<?> messageBodyWriter;
    private boolean templated;

    public BodyWriter(MessageBodyWriter<?> messageBodyWriter) {
        super(messageBodyWriter.getClass());
        templated = TemplatedMessageBodyWriter.class.isAssignableFrom(messageBodyWriter.getClass());
        this.messageBodyWriter = messageBodyWriter;
    }

    @SuppressWarnings("rawtypes") //
    public MessageBodyWriter messageBodyWriter() {
        return messageBodyWriter;
    }

    @Override //
    protected String[] getMediaTypeAnnotationValue(Class<?> clazz) {
        Produces annotation = clazz.getClass().getAnnotation(Produces.class);
        if (annotation != null) {
            return annotation.value();
        }
        return null;
    }

    @Override //
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        if (initializable()) {
            ((Initializable) messageBodyWriter).init(servletContext, routingConfig);
        }
    }

    @Override //
    public boolean isProcessable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (templated ^ (type == Templated.class)) {
            return false;
        }
        return super.isProcessable(type, genericType, annotations, mediaType)
                && messageBodyWriter.isWriteable(type, genericType, annotations, mediaType);
    }
}
