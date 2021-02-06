package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.HeaderParam;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class HeaderParamParameter extends MethodParameter {

    public HeaderParamParameter(Method method, Parameter parameter, Type genericType, int position, ConvertersHolder convertersHolder) {
        super(method, parameter, genericType, position, convertersHolder);
        if (collection()) {
            throw new RoutingInitializationException(CANT_BE_COLLECTION_MESSAGE, HeaderParam.class.getSimpleName(), method);
        }
    }

    @Override //
    public String name() {
        String name = annotation(HeaderParam.class).value();
        if ("".equals(name)) {
            return super.name();
        }
        return name;
    }

    @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {
        try {
            return convert(req.getHeader(name()));
        } catch (Throwable e) {
            throw new BadRequestException(String.format(CONVERSION_ERROR_MESSAGE, position(), type().getSimpleName(), e), e);
        }
    }
}
