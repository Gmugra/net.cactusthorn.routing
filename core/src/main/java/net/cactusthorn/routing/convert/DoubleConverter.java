package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.RequestData;

public class DoubleConverter implements Converter {

    @Override //
    public Double convert(RequestData requestData, Class<?> type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Double.valueOf(value);
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) throws ConverterException {
        if (value == null) {
            return null;
        }

        Double[] array = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(requestData, type, value[i]);
        }
        return array;
    }
}
