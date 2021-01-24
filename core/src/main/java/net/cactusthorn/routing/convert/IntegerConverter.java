package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class IntegerConverter implements Converter {

    @Override //
    public Integer convert(Class<?> type, Type genericType, Annotation[] annotations, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Integer.valueOf(value);
    }

    @Override //
    public Object convert(Class<?> type, Type genericType, Annotation[] annotations, String[] value) {
        if (value == null) {
            return null;
        }

        Integer[] array = new Integer[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(type, genericType, annotations, value[i]);
        }
        return array;
    }
}
