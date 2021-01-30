package net.cactusthorn.routing.invoke;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PathParam;

import net.cactusthorn.routing.PathTemplate.PathValues;

public class PathParamParameter implements MethodParameter {

    private ParameterInfo paramInfo;
    private String name;

    public PathParamParameter(ParameterInfo paramInfo) {
        this.paramInfo = paramInfo;

        name = paramInfo.annotation(PathParam.class).value();
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
            String value = pathValues.value(name());
            value = "".equals(value) ? null : value;
            return paramInfo.convert(value);
        } catch (Exception e) {
            throw new NotFoundException(
                    String.format(CONVERSION_ERROR_MESSAGE, paramInfo.position(), paramInfo.type().getSimpleName(), e), e);
        }
    }
}
