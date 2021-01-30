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

    private final List<ParamConverterProviderWrapper> providers = new ArrayList<>();

    private final Map<Type, Converter<?>> converters = new HashMap<>();

    public ConvertersHolder(List<ParamConverterProvider> paramConverterProviders) {
        this();
        paramConverterProviders.forEach(p -> providers.add(new ParamConverterProviderWrapper(p)));
        Collections.sort(providers, ParamConverterProviderWrapper.PRIORITY_COMPARATOR);
    }

    public ConvertersHolder() {

        PrimitiveConverter primitiveConverter = new PrimitiveConverter();
        converters.put(Byte.TYPE, primitiveConverter);
        converters.put(Short.TYPE, primitiveConverter);
        converters.put(Integer.TYPE, primitiveConverter);
        converters.put(Long.TYPE, primitiveConverter);
        converters.put(Float.TYPE, primitiveConverter);
        converters.put(Double.TYPE, primitiveConverter);
        converters.put(Character.TYPE, primitiveConverter);
        converters.put(Boolean.TYPE, primitiveConverter);

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

    public Optional<Converter<?>> findConverter(Class<?> clazz, Type genericType, Annotation[] annotations) {

        for (ParamConverterProviderWrapper wrapper : providers) {
            if (wrapper.isConvertible(clazz, genericType, annotations)) {
                return Optional.of(wrapper);
            }
        }

        Converter<?> converter = converters.get(clazz);
        if (converter != null) {
            return Optional.of(converter);
        }

        if (CONSTRUCTOR.register(clazz)) {
            converters.put(clazz, CONSTRUCTOR);
            return Optional.of(CONSTRUCTOR);
        }

        /*
         * JSR-339: If both methods(valueOf and fromString) are present then valueOf
         * MUST be used unless the type is an enum in which case fromString MUST be
         * used.
         */
        if (Enum.class.isAssignableFrom(clazz)) {
            if (FROM_STRING.register(clazz)) {
                converters.put(clazz, FROM_STRING);
                return Optional.of(FROM_STRING);
            }
            VALUE_OF.register(clazz); // enumeration always has valueOf method
            converters.put(clazz, VALUE_OF);
            return Optional.of(VALUE_OF);
        } else {
            if (VALUE_OF.register(clazz)) {
                converters.put(clazz, VALUE_OF);
                return Optional.of(VALUE_OF);
            }
            if (FROM_STRING.register(clazz)) {
                converters.put(clazz, FROM_STRING);
                return Optional.of(FROM_STRING);
            }
        }
        return Optional.empty();
    }
}
