package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.RequestData;

public class ByteConverter implements Converter {

    @Override //
    public Byte convert(RequestData requestData, Class<?> type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Byte.valueOf(value);
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) throws ConverterException {
        if (value == null) {
            return null;
        }

        Byte[] array = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(requestData, type, value[i]);
        }
        return array;
    }
}
