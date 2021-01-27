package net.cactusthorn.routing.invoke;

import java.lang.reflect.*;
import java.util.*;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConvertersHolder;

public abstract class MethodComplexParameter extends MethodParameter {

    protected static final String WRONG_TYPE_MESSAGE = "Converter for %s unknown; Method: %s";
    protected static final String WRONG_ARRAY_MESSAGE = "Multi-dimensional arrays are not supported; Method: %s";
    protected static final String CLASS_NOT_FOUND_MESSAGE = "ClassNotFound; Method: %s";
    protected static final String WRONG_COLLECTION_MESSAGE = "Wrong collection??; Method: %s";
    protected static final String COLLECTION_NO_GENERIC_MESSAGE = "Collections without generic type are not supported; Method: %s";
    protected static final String CANT_BE_COLLECTION_MESSAGE = "%s can't be collection; Method: %s";

    private Converter<?> converter;

    public MethodComplexParameter(Method method, Parameter parameter, Type parameterGenericType, ConvertersHolder convertersHolder) {
        super(parameter, parameterGenericType);
        Optional<Converter<?>> optional = convertersHolder.findConverter(classType(), parameterGenericType(),
                parameter().getAnnotations());
        if (optional.isPresent()) {
            converter = optional.get();
        } else {
            throw new RoutingInitializationException(WRONG_TYPE_MESSAGE, classType().getSimpleName(), method);
        }
    }

    public Converter<?> converter() {
        return converter;
    }

    /**
     * According to JSR-339 supported only List<T>, Set<T>, or SortedSet<T>
     */
    protected Optional<Class<?>> collectionType() {
        if (List.class.isAssignableFrom(classType())) {
            return Optional.of(ArrayList.class);
        } else if (SortedSet.class.isAssignableFrom(classType())) {
            return Optional.of(TreeSet.class);
        } else if (Set.class.isAssignableFrom(classType())) {
            return Optional.of(HashSet.class);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked") //
    protected Object createCollection(Class<?> collectionType, String[] values) throws Exception {
        Constructor<? extends Collection<Object>> constructor = (Constructor<? extends Collection<Object>>) collectionType.getConstructor();
        Collection<Object> newCollection = constructor.newInstance();
        for (String value : values) {
            newCollection.add(converter().convert(parameter().getType(), parameterGenericType(), parameter().getAnnotations(), value));
        }
        return newCollection;
    }

}
