package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Converter<T> {

    T convert(Class<?> type, Type genericType, Annotation[] annotations, String value) throws Throwable;

    default List<T> convert(Class<?> type, Type genericType, Annotation[] annotations, String[] values) throws Throwable {
        if (values == null || values.length == 0) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(values.length);
        for (String value : values) {
            result.add(convert(type, genericType, annotations, value));
        }
        return result;
    }
}
