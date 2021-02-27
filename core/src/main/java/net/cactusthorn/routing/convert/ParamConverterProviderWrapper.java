package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ParamConverterProvider;

import net.cactusthorn.routing.util.Prioritised;

public class ParamConverterProviderWrapper extends Prioritised implements Converter<Object> {

    private ParamConverterProvider provider;

    public ParamConverterProviderWrapper(ParamConverterProvider provider) {
        super(provider.getClass());
        this.provider = provider;
    }

    @Override //
    public Object convert(Class<?> type, Type genericType, Annotation[] annotations, String value) throws Exception {
        return provider.getConverter(type, genericType, annotations).fromString(value);
    }

    public boolean isConvertible(Class<?> type, Type genericType, Annotation[] annotations) {
        return provider.getConverter(type, genericType, annotations) != null;
    }
}
