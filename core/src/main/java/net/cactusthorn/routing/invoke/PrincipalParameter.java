package net.cactusthorn.routing.invoke;

import java.lang.reflect.Parameter;
import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RequestData;

public final class PrincipalParameter extends MethodParameter {

    public PrincipalParameter(Parameter parameter) {
        super(parameter);
    }

    @Override //
    Principal findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData) throws Exception {
        return req.getUserPrincipal();
    }
}
