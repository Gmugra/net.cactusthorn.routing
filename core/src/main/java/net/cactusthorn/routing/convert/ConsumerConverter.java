package net.cactusthorn.routing.convert;

import net.cactusthorn.routing.Consumer;
import net.cactusthorn.routing.RequestData;

public final class ConsumerConverter implements Converter {

    private String contentType;
    private Consumer consumer;

    ConsumerConverter(String contentType, Consumer consumer) {
        this.contentType = contentType;
        this.consumer = consumer;
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String value) throws ConverterException {
        try {
            return consumer.consume(type, contentType, requestData);
        } catch (Exception e) {
            throw new ConverterException("Consumer converting failed", e);
        }
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) throws ConverterException {
        throw new UnsupportedOperationException("Array support is senseless for consumer converting");
    }

}
