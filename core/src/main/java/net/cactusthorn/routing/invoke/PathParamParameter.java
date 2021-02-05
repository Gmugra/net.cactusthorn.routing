package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PathParam;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class PathParamParameter extends MethodParameter {

    public PathParamParameter(Method method, Parameter parameter, Type genericType, int position, ConvertersHolder convertersHolder) {
        super(method, parameter, genericType, position, convertersHolder);
        if (collection()) {
            throw new RoutingInitializationException(CANT_BE_COLLECTION_MESSAGE, PathParam.class.getSimpleName(), method);
        }
    }

    @Override //
    public String name() {
        String name = annotation(PathParam.class).value();
        if ("".equals(name)) {
            return super.name();
        }
        return name;
    }

    @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {
        try {
            String value = pathValues.value(name());
            value = "".equals(value) ? null : value;
            return convert(value);
        } catch (Exception e) {
            throw new NotFoundException(String.format(CONVERSION_ERROR_MESSAGE, position(), type().getSimpleName(), e), e);
        }
    }
}
