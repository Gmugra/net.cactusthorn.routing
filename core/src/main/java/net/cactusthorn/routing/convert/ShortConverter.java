package net.cactusthorn.routing.convert;

public class ShortConverter implements Converter {

    @Override //
    public Short convert(Class<?> type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Short.valueOf(value);
    }

    @Override //
    public Object convert(Class<?> type, String[] value) {
        if (value == null) {
            return null;
        }

        Short[] array = new Short[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(type, value[i]);
        }
        return array;
    }
}
