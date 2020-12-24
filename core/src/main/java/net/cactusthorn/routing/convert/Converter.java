package net.cactusthorn.routing.convert;

import java.lang.reflect.Array;

import net.cactusthorn.routing.RequestData;

public interface Converter {

    default Object convert(Class<?> type, String value) throws ConverterException {
        return convert(null, type, value);
    }

    default Object convert(RequestData requestData, Class<?> type) throws ConverterException {
        return convert(requestData, type, (String) null);
    }

    default Object convert(Class<?> type, String[] value) throws ConverterException {
        return convert(null, type, value);
    }

    Object convert(RequestData requestData, Class<?> type, String value) throws ConverterException;

    default Object convert(RequestData requestData, Class<?> type, String[] value) throws ConverterException {
        if (value == null) {
            return null;
        }

        Object array = Array.newInstance(type, value.length);
        for (int i = 0; i < value.length; i++) {
            Array.set(array, i, convert(requestData, type, value[i]));
        }
        return array;
    }
}
