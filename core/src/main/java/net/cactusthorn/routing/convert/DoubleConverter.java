package net.cactusthorn.routing.convert;

public class DoubleConverter implements Converter {

    @Override //
    public Double convert(Class<?> type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Double.valueOf(value);
    }

    @Override //
    public Object convert(Class<?> type, String[] value) {
        if (value == null) {
            return null;
        }

        Double[] array = new Double[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(type, value[i]);
        }
        return array;
    }
}
