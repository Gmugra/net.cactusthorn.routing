package net.cactusthorn.routing.invoke;

import java.lang.reflect.*;
import java.util.*;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public abstract class MethodComplexParameter extends MethodParameter {

    protected static final String WRONG_TYPE_MESSAGE = "Converter for %s unknown; Method: %s";
    protected static final String WRONG_ARRAY_MESSAGE = "Multi-dimensional arrays are not supported; Method: %s";
    protected static final String CLASS_NOT_FOUND_MESSAGE = "ClassNotFound; Method: %s";
    protected static final String WRONG_COLLECTION_MESSAGE = "Wrong collection??; Method: %s";
    protected static final String COLLECTION_NO_GENERIC_MESSAGE = "Collections without generic type are not supported; Method: %s";
    protected static final String CANT_BE_ARRAY_MESSAGE = "%s can't be array; Method: %s";
    protected static final String CANT_BE_COLLECTION_MESSAGE = "%s can't be collection; Method: %s";

    public MethodComplexParameter(Parameter parameter) {
        super(parameter);
    }

    protected Converter findConverter(Method method, ConvertersHolder convertersHolder) {
        return findConverter(method, classType(), convertersHolder);
    }

    protected static Converter findConverter(Method method, Class<?> converterType, ConvertersHolder convertersHolder) {
        Optional<Converter> optional = convertersHolder.findConverter(converterType);
        if (optional.isPresent()) {
            return optional.get();
        }
        throw new RoutingInitializationException(WRONG_TYPE_MESSAGE, converterType.getTypeName(), method);
    }

    protected final Optional<Class<?>> arrayType(Method method) {
        if (classType().isArray()) {
            Class<?> arrayType = classType().getComponentType();
            if (arrayType.isArray()) {
                throw new RoutingInitializationException(WRONG_ARRAY_MESSAGE, method);
            }
            return Optional.of(arrayType);
        }
        return Optional.empty();
    }

    protected Class<?> collectionGenericType(Method method, Parameter parameter) {
        Type type = parameter.getParameterizedType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] genericTypes = parameterizedType.getActualTypeArguments();
            if (genericTypes.length != 1) {
                throw new RoutingInitializationException(WRONG_COLLECTION_MESSAGE, method);
            }
            Type genericType = genericTypes[0];
            try {
                return Class.forName(genericType.getTypeName());
            } catch (ClassNotFoundException e) {
                throw new RoutingInitializationException(CLASS_NOT_FOUND_MESSAGE, e, method);
            }
        }
        throw new RoutingInitializationException(COLLECTION_NO_GENERIC_MESSAGE, method);
    }

    protected Optional<Class<?>> collectionType() {
        if (classType().isInterface()) {
            if (List.class.isAssignableFrom(classType())) {
                return Optional.of(ArrayList.class);
            } else if (SortedSet.class.isAssignableFrom(classType())) {
                return Optional.of(TreeSet.class);
            } else if (Set.class.isAssignableFrom(classType())) {
                return Optional.of(HashSet.class);
            } else if (Collection.class.isAssignableFrom(classType())) {
                return Optional.of(ArrayList.class);
            }
        } else if (!Modifier.isAbstract(classType().getModifiers()) && Collection.class.isAssignableFrom(classType())) {
            return Optional.of(classType());
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked") //
    protected Object createCollection(Class<?> collectionType, Class<?> converterType, Converter converter, String[] values)
            throws ConverterException {
        if (values == null) {
            return null;
        }

        Constructor<? extends Collection<Object>> constructor;
        try {
            constructor = (Constructor<? extends Collection<Object>>) collectionType.getConstructor();
        } catch (NoSuchMethodException | SecurityException e) {
            throw new ConverterException(e);
        }

        Collection<Object> newCollection;
        try {
            newCollection = constructor.newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new ConverterException(e);
        }

        for (String value : values) {
            newCollection.add(converter.convert(converterType, value));
        }
        return newCollection;
    }

}
