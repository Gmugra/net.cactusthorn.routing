package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

public interface Converter {

    Object convert(Class<?> type, Type genericType, Annotation[] annotations, String value) throws Exception;

    default Object convert(Class<?> type, Type genericType, Annotation[] annotations, String[] value) throws Exception {
        if (value == null) {
            return null;
        }

        Object array = Array.newInstance(type, value.length);
        for (int i = 0; i < value.length; i++) {
            Array.set(array, i, convert(type, genericType, annotations, value[i]));
        }
        return array;
    }
}
