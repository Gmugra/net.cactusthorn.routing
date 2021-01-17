package net.cactusthorn.routing.body.reader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.body.BodyProcessor;

public class BodyReader extends BodyProcessor {

    private MessageBodyReader<?> messageBodyReader;

    public BodyReader(MediaType mediaType, MessageBodyReader<?> messageBodyReader) {
        super(mediaType);
        if (messageBodyReader == null) {
            throw new IllegalArgumentException("messageBodyReader can not be null");
        }
        setInitializable(messageBodyReader instanceof InitializableMessageBodyReader);
        setPriority(messageBodyReader.getClass());
        this.messageBodyReader = messageBodyReader;
    }

    public MessageBodyReader<?> messageBodyReader() {
        return messageBodyReader;
    }

    @Override @SuppressWarnings("rawtypes") //
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        if (initializable()) {
            ((InitializableMessageBodyReader) messageBodyReader).init(servletContext, routingConfig);
        }
    }

    @Override //
    public boolean isProcessable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return messageBodyReader.isReadable(type, genericType, annotations, mediaType);
    }
}
