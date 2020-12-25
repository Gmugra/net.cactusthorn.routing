package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.RequestData;

public class FloatConverter implements Converter {

    @Override //
    public Float convert(RequestData requestData, Class<?> type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Float.valueOf(value);
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) throws ConverterException {
        if (value == null) {
            return null;
        }

        Float[] array = new Float[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(requestData, type, value[i]);
        }
        return array;
    }
}
