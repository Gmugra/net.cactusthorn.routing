package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ext.Providers;

import net.cactusthorn.routing.uri.PathTemplate.PathValues;

public class ProvidersParameter extends MethodParameter {

    private final Providers providers;

    public ProvidersParameter(Method method, Parameter parameter, Type genericType, int position, Providers providers) {
        super(method, parameter, genericType, position);
        this.providers = providers;
    }

    @Override //
    public Providers findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception {
        return providers;
    }
}
