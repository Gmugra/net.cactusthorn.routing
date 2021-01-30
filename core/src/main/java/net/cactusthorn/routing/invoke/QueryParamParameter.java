package net.cactusthorn.routing.invoke;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.QueryParam;

import net.cactusthorn.routing.PathTemplate.PathValues;

public class QueryParamParameter implements MethodParameter {

    private ParameterInfo paramInfo;
    private String name;

    public QueryParamParameter(ParameterInfo paramInfo) {
        this.paramInfo = paramInfo;

        name = paramInfo.annotation(QueryParam.class).value();
        if ("".equals(name)) {
            name = paramInfo.name();
        }
    }

    @Override //
    public String name() {
        return name;
    }

    @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {
        try {
            if (paramInfo.collection()) {
                return paramInfo.convert(req.getParameterValues(name()));
            }
            return paramInfo.convert(req.getParameter(name()));
        } catch (Exception e) {
            throw new NotFoundException(
                    String.format(CONVERSION_ERROR_MESSAGE, paramInfo.position(), paramInfo.type().getSimpleName(), e), e);
        }
    }
}
