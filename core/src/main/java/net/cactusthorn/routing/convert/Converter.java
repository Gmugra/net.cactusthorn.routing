package net.cactusthorn.routing.convert;

import java.lang.reflect.Array;

public interface Converter {

    Object convert(Class<?> type, String value) throws Exception;

    default Object convert(Class<?> type, String[] value) throws Exception {
        if (value == null) {
            return null;
        }

        Object array = Array.newInstance(type, value.length);
        for (int i = 0; i < value.length; i++) {
            Array.set(array, i, convert(type, value[i]));
        }
        return array;
    }
}
