package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;

import net.cactusthorn.routing.uri.PathTemplate.PathValues;

public class ApplicationParameter extends MethodParameter {

    private final Application application;

    public ApplicationParameter(Method method, Parameter parameter, Type genericType, int position, Application application) {
        super(method, parameter, genericType, position);
        this.application = application;
    }

    @Override //
    public Application findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception {
        return application;
    }
}
