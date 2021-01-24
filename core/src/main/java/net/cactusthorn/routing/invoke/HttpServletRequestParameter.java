package net.cactusthorn.routing.invoke;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.PathTemplate.PathValues;

public final class HttpServletRequestParameter extends MethodParameter {

    public HttpServletRequestParameter(Parameter parameter, Type parameterGenericType) {
        super(parameter, parameterGenericType);
    }

    @Override //
    HttpServletRequest findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception {
        return req;
    }
}
