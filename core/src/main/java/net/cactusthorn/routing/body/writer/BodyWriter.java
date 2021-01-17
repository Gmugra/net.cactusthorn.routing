package net.cactusthorn.routing.body.writer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.body.BodyProcessor;

public class BodyWriter extends BodyProcessor {

    private MessageBodyWriter<?> messageBodyWriter;

    public BodyWriter(MediaType mediaType, MessageBodyWriter<?> messageBodyWriter) {
        super(mediaType);
        if (messageBodyWriter == null) {
            throw new IllegalArgumentException("messageBodyWriter can not be null");
        }
        setInitializable(messageBodyWriter instanceof InitializableMessageBodyWriter);
        setPriority(messageBodyWriter.getClass());
        this.messageBodyWriter = messageBodyWriter;
    }

    @SuppressWarnings("rawtypes") //
    public MessageBodyWriter messageBodyWriter() {
        return messageBodyWriter;
    }

    @Override @SuppressWarnings("rawtypes") //
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        if (initializable()) {
            ((InitializableMessageBodyWriter) messageBodyWriter).init(servletContext, routingConfig);
        }
    }

    @Override //
    public boolean isProcessable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return messageBodyWriter.isWriteable(type, genericType, annotations, mediaType);
    }

    //public static findBodyWriter
}
