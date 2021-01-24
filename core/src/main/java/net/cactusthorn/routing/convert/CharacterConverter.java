package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class CharacterConverter implements Converter {

    @Override //
    public Character convert(Class<?> type, Type genericType, Annotation[] annotations, String value) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return Character.MIN_VALUE;
        }
        return value.charAt(0);
    }

    @Override //
    public Object convert(Class<?> type, Type genericType, Annotation[] annotations, String[] value) {
        if (value == null) {
            return null;
        }

        Character[] array = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(type, genericType, annotations, value[i]);
        }
        return array;
    }
}
