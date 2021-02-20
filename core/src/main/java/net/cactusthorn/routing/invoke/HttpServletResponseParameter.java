package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.uri.PathTemplate.PathValues;

public final class HttpServletResponseParameter extends MethodParameter {

    public HttpServletResponseParameter(Method method, Parameter parameter, Type genericType, int position) {
        super(method, parameter, genericType, position);
    }

    @Override //
    public HttpServletResponse findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception {
        return res;
    }
}
