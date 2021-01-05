package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConvertersHolder;

public abstract class MethodSingleValueParameter extends MethodComplexParameter {

    private Converter converter;

    public MethodSingleValueParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder) {
        super(parameter);

        Optional<Class<?>> optionalArray = arrayType(method);
        if (optionalArray.isPresent()) {
            throw new RoutingInitializationException(CANT_BE_ARRAY_MESSAGE, annotationName(), method);
        }

        Optional<Class<?>> optionalCollection = collectionType();
        if (optionalCollection.isPresent()) {
            throw new RoutingInitializationException(CANT_BE_COLLECTION_MESSAGE, annotationName(), method);
        }

        converter = findConverter(method, convertersHolder);
    }

    protected Converter converter() {
        return converter;
    }

    protected abstract String annotationName();
}
