package net.cactusthorn.routing.convert;

public final class StringConverter implements Converter {

    @Override //
    public String convert(Class<?> type, String value) {
        return value;
    }

    @Override //
    public Object convert(Class<?> type, String[] value) {
        if (value == null) {
            return null;
        }

        String[] array = new String[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(type, value[i]);
        }
        return array;
    }
}
