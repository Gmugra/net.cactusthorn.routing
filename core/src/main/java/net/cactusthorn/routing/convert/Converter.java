package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface Converter<T> {

    T convert(Class<?> type, Type genericType, Annotation[] annotations, String value) throws Throwable;
}
