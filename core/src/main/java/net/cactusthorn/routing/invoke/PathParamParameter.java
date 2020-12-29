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
import net.cactusthorn.routing.convert.ConvertersHolder;

public class PathParamParameter extends MethodComplexParameter {

    private String name;
    private Converter converter;

    public PathParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder) {
        super(parameter);
        name = initName(parameter);

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

    protected String name() {
        return name;
    }

    protected String annotationName() {
        return PathParam.class.getSimpleName();
    }

    protected String initName(Parameter parameter) {
        PathParam pathParam = parameter.getAnnotation(PathParam.class);
        return pathParam.value();
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData) throws Exception {
        String value = requestData.pathValues().value(name);
        if (defaultValue() != null && "".equals(value)) {
            value = defaultValue();
        }
        return converter.convert(classType(), value);
    }
}
