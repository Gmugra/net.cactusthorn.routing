package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.QueryParam;

import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class QueryParamParameter extends MethodParameter {

    public QueryParamParameter(Method method, Parameter parameter, Type genericType, int position, ConvertersHolder convertersHolder) {
        super(method, parameter, genericType, position, convertersHolder);
    }

    @Override //
    public String name() {
        String name = annotation(QueryParam.class).value();
        if ("".equals(name)) {
            return super.name();
        }
        return name;
    }

    @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {
        try {
            if (collection()) {
                return convert(req.getParameterValues(name()));
            }
            return convert(req.getParameter(name()));
        } catch (Exception e) {
            throw new NotFoundException(String.format(CONVERSION_ERROR_MESSAGE, position(), type().getSimpleName(), e), e);
        }
    }
}
