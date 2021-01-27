package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class CharacterConverter implements Converter<Character> {

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
}
