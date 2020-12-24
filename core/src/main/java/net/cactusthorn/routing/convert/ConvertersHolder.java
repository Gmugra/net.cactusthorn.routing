package net.cactusthorn.routing.convert;

import java.util.HashMap;
import java.util.Map;

import net.cactusthorn.routing.Consumer;

public class ConvertersHolder {

    private static final NullConverter NULL = new NullConverter();
    private static final ValueOfConverter VALUE_OF = new ValueOfConverter();

    private final Map<Class<?>, Converter> converters = new HashMap<>();

    private final Map<String, ConsumerConverter> consumers = new HashMap<>();

    public ConvertersHolder() {

        converters.put(Byte.TYPE, new PrimitiveConverter());
        converters.put(Short.TYPE, new PrimitiveConverter());
        converters.put(Integer.TYPE, new PrimitiveConverter());
        converters.put(Long.TYPE, new PrimitiveConverter());
        converters.put(Float.TYPE, new PrimitiveConverter());
        converters.put(Double.TYPE, new PrimitiveConverter());
        converters.put(Character.TYPE, new PrimitiveConverter());
        converters.put(Boolean.TYPE, new PrimitiveConverter());

        converters.put(Integer.class, new IntegerConverter());
        converters.put(String.class, new StringConverter());
    }

    public Converter findConverter(Class<?> clazz) {
        Converter converter = converters.get(clazz);
        if (converter != null) {
            return converter;
        }
        if (VALUE_OF.register(clazz)) {
            converters.put(clazz, VALUE_OF);
            return VALUE_OF;
        }
        return NULL;
    }

    public Converter findConsumerConverter(String contentType) {
        ConsumerConverter converter = consumers.get(contentType);
        if (converter != null) {
            return converter;
        }
        return NULL;
    }

    public void register(Class<?> clazz, Converter converter) {
        converters.put(clazz, converter);
    }

    public <T> void register(String contentType, Consumer consumer) {
        consumers.put(contentType, new ConsumerConverter(contentType, consumer));
    }
}
