package net.cactusthorn.routing.converter;

public class NullConverter implements Converter<Object> {

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String value) {
        return null;
    }
}
