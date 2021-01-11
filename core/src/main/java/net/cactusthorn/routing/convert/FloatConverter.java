package net.cactusthorn.routing.convert;

public class FloatConverter implements Converter {

    @Override //
    public Float convert(Class<?> type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Float.valueOf(value);
    }

    @Override //
    public Object convert(Class<?> type, String[] value) {
        if (value == null) {
            return null;
        }

        Float[] array = new Float[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(type, value[i]);
        }
        return array;
    }
}
