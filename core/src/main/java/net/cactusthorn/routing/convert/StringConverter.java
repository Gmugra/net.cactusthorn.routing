package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public final class StringConverter implements Converter<String> {

    @Override //
    public String convert(Class<?> type, Type genericType, Annotation[] annotations, String value) {
        return value;
    }
}
