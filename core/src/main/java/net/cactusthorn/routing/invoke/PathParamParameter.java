package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public final class PathParamParameter extends MethodComplexParameter {

    private String name;
    private Converter converter;

    public PathParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder) {
        super(parameter);
        PathParam pathParam = parameter.getAnnotation(PathParam.class);
        name = pathParam.value();

        Optional<Class<?>> optionalArray = arrayType(method);
        if (optionalArray.isPresent()) {
            throw new RoutingInitializationException(CANT_BE_ARRAY_MESSAGE, PathParam.class.getSimpleName(), method);
        }

        Optional<Class<?>> optionalCollection = collectionType();
        if (optionalCollection.isPresent()) {
            throw new RoutingInitializationException(CANT_BE_COLLECTION_MESSAGE, PathParam.class.getSimpleName(), method);
        }

        converter = findConverter(method, convertersHolder);
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws ConverterException {
        try {
            return converter.convert(classType(), requestData.pathValues().value(name));
        } catch (ConverterException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ConverterException("Type Converting problem", e);
        }
    }
}
