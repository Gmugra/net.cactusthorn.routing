package net.cactusthorn.routing.invoke;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.HeaderParam;

import net.cactusthorn.routing.PathTemplate.PathValues;

public class HeaderParamParameter implements MethodParameter {

    private ParameterInfo paramInfo;
    private String name;

    public HeaderParamParameter(ParameterInfo paramInfo) {
        this.paramInfo = paramInfo;

        name = paramInfo.annotation(HeaderParam.class).value();
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
            return paramInfo.convert(req.getHeader(name()));
        } catch (Exception e) {
            throw new BadRequestException(
                    String.format(CONVERSION_ERROR_MESSAGE, paramInfo.position(), paramInfo.type().getSimpleName(), e), e);
        }
    }

}
