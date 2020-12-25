package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.RequestData;

public class LongConverter implements Converter {

    @Override //
    public Long convert(RequestData requestData, Class<?> type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Long.valueOf(value);
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) throws ConverterException {
        if (value == null) {
            return null;
        }

        Long[] array = new Long[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(requestData, type, value[i]);
        }
        return array;
    }
}
