package net.cactusthorn.routing.converter;

import net.cactusthorn.routing.RequestData;

public interface Converter<T> {

    default T convert(Class<?> type, String value) throws ConverterException {
        return convert(null, type, value);
    }

    default T convert(RequestData requestData, Class<?> type) throws ConverterException {
        return convert(requestData, type, null);
    }

    default T convert(String value) throws ConverterException {
        return convert(null, null, value);
    }

    T convert(RequestData requestData, Class<?> type, String value) throws ConverterException;
}
