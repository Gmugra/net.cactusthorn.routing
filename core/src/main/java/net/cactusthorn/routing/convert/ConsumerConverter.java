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
    public Object convert(RequestData requestData, Class<?> type, String value) {
        return consumer.consume(type, contentType, requestData);
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) {
        throw new UnsupportedOperationException("Array support is senseless for consumer converting");
    }

}
