package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.RequestData;

public class ShortConverter implements Converter {

    @Override //
    public Short convert(RequestData requestData, Class<?> type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Short.valueOf(value);
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) {
        if (value == null) {
            return null;
        }

        Short[] array = new Short[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(requestData, type, value[i]);
        }
        return array;
    }
}
