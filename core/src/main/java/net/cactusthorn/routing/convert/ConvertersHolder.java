package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import javax.ws.rs.ext.ParamConverterProvider;

public class ConvertersHolder {

    private static final StaticStringMethodConverter VALUE_OF = new StaticStringMethodConverter("valueOf");
    private static final StaticStringMethodConverter FROM_STRING = new StaticStringMethodConverter("fromString");
    private static final StringConstructorConverter CONSTRUCTOR = new StringConstructorConverter();

    private final List<ParamConverterProviderWrapper> providers = new ArrayList<>();;

    private final Map<Type, Converter> converters = new HashMap<>();

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

    public Optional<Converter> findConverter(Class<?> clazz, Type genericType, Annotation[] annotations) {

        for (ParamConverterProviderWrapper wrapper : providers) {
            if (wrapper.isConvertible(clazz, genericType, annotations)) {
                return Optional.of(wrapper);
            }
        }

        Converter converter = converters.get(clazz);
        if (converter != null) {
            return Optional.of(converter);
        }
        if (VALUE_OF.register(clazz)) {
            converters.put(clazz, VALUE_OF);
            return Optional.of(VALUE_OF);
        }
        if (CONSTRUCTOR.register(clazz)) {
            converters.put(clazz, CONSTRUCTOR);
            return Optional.of(CONSTRUCTOR);
        }
        if (FROM_STRING.register(clazz)) {
            converters.put(clazz, FROM_STRING);
            return Optional.of(FROM_STRING);
        }
        return Optional.empty();
    }

    public void addProviders(List<ParamConverterProvider> paramConverterProviders) {
        paramConverterProviders.forEach(p -> providers.add(new ParamConverterProviderWrapper(p)));
        Collections.sort(providers, ParamConverterProviderWrapper.PRIORITY_COMPARATOR);
    }
}
