package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.RequestData;

public class NullConverter implements Converter {

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String value) {
        return null;
    }

    @Override //
    public Object[] convert(RequestData requestData, Class<?> type, String[] value) {
        return null;
    }
}
