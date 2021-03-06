package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriInfo;

import net.cactusthorn.routing.uri.PathTemplate.PathValues;
import net.cactusthorn.routing.uri.UriInfoImpl;

public class UriInfoParameter extends MethodParameter {

    public UriInfoParameter(Method method, Parameter parameter, Type genericType, int position) {
        super(method, parameter, genericType, position);
    }

    @Override //
    public UriInfo findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) throws Exception {
        return new UriInfoImpl(req, pathValues);
    }
}
