package net.cactusthorn.routing.body;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MediaType;

public abstract class BodyProcessor implements Initializable {

    public static final Comparator<BodyProcessor> PRIORITY_COMPARATOR = (bp1, bp2) -> {
        if (bp1 == null && bp2 == null) {
            return 0;
        }
        if (bp1 == null) {
            return 1;
        }
        if (bp2 == null) {
            return -1;
        }
        return bp1.priority() - bp2.priority();
    };

    public static final int LOWEST_PRIORITY = 9999;
    public static final int PRIORITY_HIGHEST = 50;

    private int priority;

    private Set<MediaType> mmediaTypes;

    private boolean initializable;

    private Class<?> bodyProcessorClass;

    public BodyProcessor(Class<?> bodyProcessorClass) {
        this.bodyProcessorClass = bodyProcessorClass;
        initializable = Initializable.class.isAssignableFrom(bodyProcessorClass);
        priority = findPriority(bodyProcessorClass);
        mmediaTypes = findMediaTypes(bodyProcessorClass);
    }

    public int priority() {
        return priority;
    }

    public boolean initializable() {
        return initializable;
    }

    public boolean isProcessable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (mediaType == null) {
            return true;
        }
        for (MediaType value : mmediaTypes) {
            if (mediaType.isCompatible(value)) {
                return true;
            }
        }
        return false;
    }

    @Override //
    public String toString() {
        return priority + " :: " + bodyProcessorClass.getName();
    }

    protected abstract String[] getMediaTypeAnnotationValue(Class<?> clazz);

    private int findPriority(Class<?> clazz) {
        Priority annotation = clazz.getAnnotation(Priority.class);
        if (annotation != null) {
            return annotation.value();
        }
        return Priorities.USER;
    }

    private Set<MediaType> findMediaTypes(Class<?> clazz) {
        String[] types = getMediaTypeAnnotationValue(clazz);
        if (types != null) {
            return parseMediaTypes(types);
        }
        Set<MediaType> result = new HashSet<>();
        result.add(MediaType.WILDCARD_TYPE);
        return result;
    }

    private Set<MediaType> parseMediaTypes(String[] types) {
        Set<MediaType> result = new HashSet<>();
        for (String value : types) {
            for (String subValue : value.split(",")) {
                String[] parts = subValue.trim().split(";")[0].split("/"); // cut all parameters
                result.add(new MediaType(parts[0], parts[1]));
            }
        }
        return result;
    }
}
