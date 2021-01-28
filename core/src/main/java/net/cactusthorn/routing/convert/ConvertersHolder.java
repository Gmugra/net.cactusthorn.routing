package net.cactusthorn.routing.convert;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
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

        Class<?> type = clazz;
        Type generic = genericType;
        if (genericType != null && collectionClass(clazz)) {
            Class<?> collectionGenericType = collectionGenericType(genericType);
            if (collectionGenericType != null) {
                type = collectionGenericType;
                generic = collectionGenericType;
            }
        }

        for (ParamConverterProviderWrapper wrapper : providers) {
            if (wrapper.isConvertible(type, generic, annotations)) {
                return Optional.of(wrapper);
            }
        }

        Converter<?> converter = converters.get(type);
        if (converter != null) {
            return Optional.of(converter);
        }

        if (CONSTRUCTOR.register(type)) {
            converters.put(type, CONSTRUCTOR);
            return Optional.of(CONSTRUCTOR);
        }

        /*
         * JSR-339: If both methods(valueOf and fromString) are present then valueOf
         * MUST be used unless the type is an enum in which case fromString MUST be
         * used.
         */
        if (Enum.class.isAssignableFrom(type)) {
            if (FROM_STRING.register(type)) {
                converters.put(type, FROM_STRING);
                return Optional.of(FROM_STRING);
            }
            VALUE_OF.register(type); // enumeration always has valueOf method
            converters.put(type, VALUE_OF);
            return Optional.of(VALUE_OF);
        } else {
            if (VALUE_OF.register(type)) {
                converters.put(type, VALUE_OF);
                return Optional.of(VALUE_OF);
            }
            if (FROM_STRING.register(type)) {
                converters.put(type, FROM_STRING);
                return Optional.of(FROM_STRING);
            }
        }
        return Optional.empty();
    }

    /**
     * According to JSR-339 supported only List<T>, Set<T>, or SortedSet<T>
     */
    private boolean collectionClass(Class<?> clazz) {
        return List.class == clazz || SortedSet.class == clazz || Set.class == clazz;
    }

    private Class<?> collectionGenericType(Type parameterGenericType) {
        if (parameterGenericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) parameterGenericType;
            Type[] genericTypes = parameterizedType.getActualTypeArguments();
            return (Class<?>) genericTypes[0];
        }
        return null;
    }
}
