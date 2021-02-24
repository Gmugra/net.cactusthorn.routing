package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.QueryParam;

import net.cactusthorn.routing.convert.ConvertersHolder;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;

import net.cactusthorn.routing.util.Messages;
import static net.cactusthorn.routing.util.Messages.Key.ERROR_AT_PARAMETER_POSITION;

public class QueryParamParameter extends ConvertableMethodParameter {

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
        } catch (Throwable e) {
            throw new NotFoundException(Messages.msg(ERROR_AT_PARAMETER_POSITION, position(), type().getSimpleName(), e), e);
        }
    }
}
