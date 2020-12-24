package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.RequestData;

public final class StringConverter implements Converter {

    @Override //
    public String convert(RequestData requestData, Class<?> type, String value) {
        return value;
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) throws ConverterException {
        if (value == null) {
            return null;
        }

        String[] array = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(requestData, type, value[i]);
        }
        return array;
    }
}
