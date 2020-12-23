package net.cactusthorn.routing.converter;

import net.cactusthorn.routing.RequestData;

public class StringConverter implements Converter<String> {

    @Override //
    public String convert(RequestData requestData, Class<?> type, String value) {
        return value;
    }
}
