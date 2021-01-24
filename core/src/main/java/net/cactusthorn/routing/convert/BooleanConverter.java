package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class BooleanConverter implements Converter {

    @Override //
    public Boolean convert(Class<?> type, Type genericType, Annotation[] annotations, String value) {
        if (value == null) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(value);
    }

    @Override //
    public Object convert(Class<?> type, Type genericType, Annotation[] annotations, String[] value) {
        if (value == null) {
            return null;
        }

        Boolean[] array = new Boolean[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(type, genericType, annotations, value[i]);
        }
        return array;
    }
}
