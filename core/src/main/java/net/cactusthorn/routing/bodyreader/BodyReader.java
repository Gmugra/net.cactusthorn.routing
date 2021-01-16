package net.cactusthorn.routing.bodyreader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Comparator;

import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;

import net.cactusthorn.routing.RoutingConfig;

public class BodyReader {

    public static final Comparator<BodyReader> COMPARATOR = (r1, r2) -> {
        if (r1 == null && r2 == null) {
            return 0;
        }
        if (r1 == null) {
            return 1;
        }
        if (r2 == null) {
            return -1;
        }

        int priority = r1.priority() - r2.priority();
        if (priority != 0) {
            return priority;
        }

        if (r1.mediaType().isWildcardType() && !r2.mediaType().isWildcardType()) {
            return 1;
        }
        if (!r1.mediaType().isWildcardType() && r2.mediaType().isWildcardType()) {
            return -1;
        }
        if (r1.mediaType().isWildcardSubtype() && !r2.mediaType().isWildcardSubtype()) {
            return 1;
        }
        if (!r1.mediaType().isWildcardSubtype() && r2.mediaType().isWildcardSubtype()) {
            return -1;
        }

        return 0;
    };

    public static final int LOWEST_PRIORITY = 9999;

    private MessageBodyReader<?> messageBodyReader;

    private int priority = Priorities.USER;

    private MediaType mmediaType;

    private boolean initializable;

    public BodyReader(MediaType mediaType, MessageBodyReader<?> messageBodyReader) {
        if (mediaType == null) {
            throw new IllegalArgumentException("mediaType can not be null");
        }
        if (messageBodyReader == null) {
            throw new IllegalArgumentException("messageBodyReader can not be null");
        }

        this.mmediaType = new MediaType(mediaType.getType(), mediaType.getSubtype()); // to ignore parameters

        this.messageBodyReader = messageBodyReader;
        if (messageBodyReader instanceof InitializableMessageBodyReader) {
            this.initializable = true;
        }
        Priority annotation = messageBodyReader.getClass().getAnnotation(Priority.class);
        if (annotation != null) {
            this.priority = annotation.value();
        }
    }

    public MessageBodyReader<?> messageBodyReader() {
        return messageBodyReader;
    }

    public MediaType mediaType() {
        return mmediaType;
    }

    public int priority() {
        return priority;
    }

    public boolean initializable() {
        return initializable;
    }

    @SuppressWarnings("rawtypes") //
    public void init(ServletContext servletContext, RoutingConfig routingConfig) {
        if (initializable) {
            ((InitializableMessageBodyReader) messageBodyReader).init(servletContext, routingConfig);
        }
    }

    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return messageBodyReader.isReadable(type, genericType, annotations, mediaType);
    }

    @Override //
    public String toString() {
        return mediaType().toString() + "::" + priority();
    }
}
