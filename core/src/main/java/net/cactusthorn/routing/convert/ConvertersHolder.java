package net.cactusthorn.routing.convert;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.cactusthorn.routing.Consumer;

public class ConvertersHolder {

    private static final ValueOfConverter VALUE_OF = new ValueOfConverter();

    private final Map<Type, Converter> converters = new HashMap<>();

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

        converters.put(String.class, new StringConverter());
        converters.put(Byte.class, new ByteConverter());
        converters.put(Short.class, new ShortConverter());
        converters.put(Integer.class, new IntegerConverter());
        converters.put(Long.class, new LongConverter());
        converters.put(Float.class, new FloatConverter());
        converters.put(Double.class, new DoubleConverter());
        converters.put(Character.class, new CharacterConverter());
        converters.put(Boolean.class, new BooleanConverter());
    }

    public Optional<Converter> findConverter(Class<?> clazz) {
        Converter converter = converters.get(clazz);
        if (converter != null) {
            return Optional.of(converter);
        }
        if (VALUE_OF.register(clazz)) {
            converters.put(clazz, VALUE_OF);
            return Optional.of(VALUE_OF);
        }
        return Optional.empty();
    }

    public Optional<ConsumerConverter> findConsumerConverter(String contentType) {
        return Optional.ofNullable(consumers.get(contentType));
    }

    public void register(Class<?> clazz, Converter converter) {
        converters.put(clazz, converter);
    }

    public <T> void register(String contentType, Consumer consumer) {
        consumers.put(contentType, new ConsumerConverter(contentType, consumer));
    }
}
