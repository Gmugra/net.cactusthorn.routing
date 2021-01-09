package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.RoutingInitializationException;

public class CookieParamParameter extends MethodParameter {

    protected static final String WRONG_TYPE = "@CookieParam can be used only for javax.servlet.http.Cookie type; Method: %s";

    public CookieParamParameter(Method method, Parameter parameter) {
        super(parameter);
        if (classType() != Cookie.class) {
            throw new RoutingInitializationException(WRONG_TYPE, method);
        }
    }

    @Override //
    protected String findName(Parameter parameter) {
        String name = parameter.getAnnotation(CookieParam.class).value();
        if ("".equals(name)) {
            return super.findName(parameter);
        }
        return name;
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData) throws Exception {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name().equals(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
}
