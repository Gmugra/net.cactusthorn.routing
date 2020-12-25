package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.RequestData;

public class CharacterConverter implements Converter {

    @Override //
    public Character convert(RequestData requestData, Class<?> type, String value) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return Character.MIN_VALUE;
        }
        return value.charAt(0);
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) throws ConverterException {
        if (value == null) {
            return null;
        }

        Character[] array = new Character[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(requestData, type, value[i]);
        }
        return array;
    }
}
