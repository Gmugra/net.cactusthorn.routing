package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Comparator;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.ParamConverterProvider;

public class ParamConverterProviderWrapper implements Converter<Object> {

    public static final Comparator<ParamConverterProviderWrapper> PRIORITY_COMPARATOR = (w1, w2) -> {
        if (w1 == null && w2 == null) {
            return 0;
        }
        if (w1 == null) {
            return 1;
        }
        if (w2 == null) {
            return -1;
        }
        return w1.priority() - w2.priority();
    };

    private ParamConverterProvider provider;

    private int priority;

    public ParamConverterProviderWrapper(ParamConverterProvider provider) {
        this.provider = provider;
        priority = findPriority(provider.getClass());
    }

    public int priority() {
        return priority;
    }

    @Override //
    public Object convert(Class<?> type, Type genericType, Annotation[] annotations, String value) throws Exception {
        return provider.getConverter(type, genericType, annotations).fromString(value);
    }

    public boolean isConvertible(Class<?> type, Type genericType, Annotation[] annotations) {
        return provider.getConverter(type, genericType, annotations) != null;
    }

    private int findPriority(Class<?> clazz) {
        Priority annotation = clazz.getAnnotation(Priority.class);
        if (annotation != null) {
            return annotation.value();
        }
        return Priorities.USER;
    }
}
