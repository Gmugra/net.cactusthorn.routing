package net.cactusthorn.routing.convert;

public class ByteConverter implements Converter {

    @Override //
    public Byte convert(Class<?> type, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Byte.valueOf(value);
    }

    @Override //
    public Object convert(Class<?> type, String[] value) {
        if (value == null) {
            return null;
        }

        Byte[] array = new Byte[value.length];
        for (int i = 0; i < value.length; i++) {
            array[i] = convert(type, value[i]);
        }
        return array;
    }
}
