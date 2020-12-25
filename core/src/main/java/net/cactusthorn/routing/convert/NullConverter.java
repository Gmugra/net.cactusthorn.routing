package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.RequestData;

public final class NullConverter implements Converter {

    public static final NullConverter NULL = new NullConverter();

    private PrimitiveConverter primitiveConverter = new PrimitiveConverter();

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String value) throws ConverterException {
        if (type.isPrimitive()) {
            return primitiveConverter.convert(type, (String) null);
        }
        return null;
    }

    @Override //
    public Object[] convert(RequestData requestData, Class<?> type, String[] value) {
        return null;
    }
}
