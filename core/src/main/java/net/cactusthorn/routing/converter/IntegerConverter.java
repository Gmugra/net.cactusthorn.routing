package net.cactusthorn.routing.converter;

public class IntegerConverter implements Converter<Integer> {

    @Override //
    public Integer convert(RequestData requestData, Class<?> type, String value) {
        if (value == null) {
            return null;
        }
        return Integer.valueOf(value);
    }

}
