package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.CookieParam;
import net.cactusthorn.routing.convert.ConverterException;

public class CookieParamParameter extends MethodParameter {

    protected static final String WRONG_TYPE = "@CookieParam can be used only for javax.servlet.http.Cookie type; Method: %s";

    private String name;

    public CookieParamParameter(Method method, Parameter parameter) {
        super(parameter);
        CookieParam cookieParam = parameter.getAnnotation(CookieParam.class);
        name = cookieParam.value();
        if (classType() != Cookie.class) {
            throw new RoutingInitializationException(WRONG_TYPE, method);
        }
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws ConverterException {
        try {
            Cookie[] cookies = req.getCookies();
            if (cookies == null) {
                return null;
            }
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
            return null;
        } catch (Exception e) {
            throw new ConverterException("Type Converting problem", e);
        }
    }
}