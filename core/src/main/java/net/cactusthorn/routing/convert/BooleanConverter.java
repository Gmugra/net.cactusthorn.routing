package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class BooleanConverter implements Converter<Boolean> {

    @Override //
    public Boolean convert(Class<?> type, Type genericType, Annotation[] annotations, String value) {
        if (value == null) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(value);
    }
}
