package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.RequestData;

public class BooleanConverter implements Converter {

    @Override //
    public Boolean convert(RequestData requestData, Class<?> type, String value) {
        if (value == null) {
            return Boolean.FALSE;
        }
        return Boolean.valueOf(value);
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) {
        if (value == null) {
            return null;
        }

        Boolean[] array = new Boolean[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(requestData, type, value[i]);
        }
        return array;
    }
}
