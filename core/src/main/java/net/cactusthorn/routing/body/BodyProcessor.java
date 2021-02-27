package net.cactusthorn.routing.body;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.MediaType;

import net.cactusthorn.routing.util.Prioritised;

public abstract class BodyProcessor extends Prioritised implements Initializable {

    private Set<MediaType> mmediaTypes;

    private boolean initializable;

    private Class<?> bodyProcessorClass;

    public BodyProcessor(Class<?> bodyProcessorClass) {
        super(bodyProcessorClass);
        this.bodyProcessorClass = bodyProcessorClass;
        initializable = Initializable.class.isAssignableFrom(bodyProcessorClass);
        mmediaTypes = findMediaTypes(bodyProcessorClass);
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
        return priority() + " :: " + bodyProcessorClass.getName();
    }

    protected abstract String[] getMediaTypeAnnotationValue(Class<?> clazz);

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
