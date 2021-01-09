package net.cactusthorn.routing.convert;

import javax.ws.rs.core.MediaType;

import net.cactusthorn.routing.Consumer;
import net.cactusthorn.routing.RequestData;

public final class ConsumerConverter implements Converter {

    private MediaType mediaType;
    private Consumer consumer;

    ConsumerConverter(MediaType mediaType, Consumer consumer) {
        this.mediaType = mediaType;
        this.consumer = consumer;
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String value) {
        return consumer.consume(type, mediaType, requestData);
    }

    @Override //
    public Object convert(RequestData requestData, Class<?> type, String[] value) {
        throw new UnsupportedOperationException("Array support is senseless for consumer converting");
    }

}
