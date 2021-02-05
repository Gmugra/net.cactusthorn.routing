package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;

public class CookieParamParameter extends MethodParameter {

    protected static final String WRONG_TYPE = "@CookieParam can be used only for javax.servlet.http.Cookie type; Method: %s";

    public CookieParamParameter(Method method, Parameter parameter, Type genericType, int position) {
        super(method, parameter, genericType, position);
        if (type() != Cookie.class) {
            throw new RoutingInitializationException(WRONG_TYPE, method());
        }
    }

    @Override //
    public String name() {
        String name = annotation(CookieParam.class).value();
        if ("".equals(name)) {
            return super.name();
        }
        return name;
    }

    @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {
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
