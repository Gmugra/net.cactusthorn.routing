package net.cactusthorn.routing.converter;

import net.cactusthorn.routing.Consumer;
import net.cactusthorn.routing.RequestData;

public class ConsumerConverter implements Converter<Object> {

    private String contentType;
    private Consumer consumer;

    public ConsumerConverter(String contentType, Consumer consumer) {
        this.contentType = contentType;
        this.consumer = consumer;
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String value) throws ConverterException {
        try {
            return consumer.consume(type, contentType, requestData);
        } catch (Exception e) {
            throw new ConverterException("Consumer convert problem", e);
        }
    }

}
