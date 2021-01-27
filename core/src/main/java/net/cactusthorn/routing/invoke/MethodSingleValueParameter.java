package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public abstract class MethodSingleValueParameter extends MethodComplexParameter {

    public MethodSingleValueParameter(Method method, Parameter parameter, Type parameterGenericType, ConvertersHolder convertersHolder) {
        super(method, parameter, parameterGenericType, convertersHolder);

        Optional<Class<?>> optionalCollection = collectionType();
        if (optionalCollection.isPresent()) {
            throw new RoutingInitializationException(CANT_BE_COLLECTION_MESSAGE, annotationName(), method);
        }
    }

    protected abstract String annotationName();
}
