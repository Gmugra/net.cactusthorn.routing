package net.cactusthorn.routing.body;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Comparator;

import javax.annotation.Priority;
import javax.servlet.ServletContext;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;

import net.cactusthorn.routing.RoutingConfig;

public abstract class BodyProcessor {

    public static final Comparator<BodyProcessor> COMPARATOR = (b1, b2) -> {
        if (b1 == null && b2 == null) {
            return 0;
        }
        if (b1 == null) {
            return 1;
        }
        if (b2 == null) {
            return -1;
        }

        int priority = b1.priority() - b2.priority();
        if (priority != 0) {
            return priority;
        }

        if (b1.mediaType().isWildcardType() && !b2.mediaType().isWildcardType()) {
            return 1;
        }
        if (!b1.mediaType().isWildcardType() && b2.mediaType().isWildcardType()) {
            return -1;
        }
        if (b1.mediaType().isWildcardSubtype() && !b2.mediaType().isWildcardSubtype()) {
            return 1;
        }
        if (!b1.mediaType().isWildcardSubtype() && b2.mediaType().isWildcardSubtype()) {
            return -1;
        }

        return 0;
    };

    public static final int LOWEST_PRIORITY = 9999;

    private int priority;

    private MediaType mmediaType;

    private boolean initializable;

    public BodyProcessor(MediaType mediaType) {
        if (mediaType == null) {
            throw new IllegalArgumentException("mediaType can not be null");
        }
        this.mmediaType = new MediaType(mediaType.getType(), mediaType.getSubtype()); // to ignore parameters
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

    protected void setInitializable(boolean value) {
        this.initializable = value;
    }

    protected void setPriority(Class<?> clazz) {
        Priority annotation = clazz.getAnnotation(Priority.class);
        if (annotation != null) {
            this.priority = annotation.value();
        } else {
            this.priority = Priorities.USER;
        }
    }

    public abstract void init(ServletContext servletContext, RoutingConfig routingConfig);

    public abstract boolean isProcessable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType);

    @Override //
    public String toString() {
        return mediaType().toString() + "::" + priority();
    }
}
