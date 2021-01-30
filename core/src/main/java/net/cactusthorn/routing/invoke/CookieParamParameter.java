package net.cactusthorn.routing.invoke;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.CookieParam;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;

public class CookieParamParameter implements MethodParameter {

    protected static final String WRONG_TYPE = "@CookieParam can be used only for javax.servlet.http.Cookie type; Method: %s";

    private String name;

    public CookieParamParameter(ParameterInfo paramInfo) {
        if (paramInfo.type() != Cookie.class) {
            throw new RoutingInitializationException(WRONG_TYPE, paramInfo.method());
        }

        name = paramInfo.annotation(CookieParam.class).value();
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
