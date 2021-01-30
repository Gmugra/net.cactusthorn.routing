package net.cactusthorn.routing.invoke;

import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.PathTemplate.PathValues;

public final class PrincipalParameter implements MethodParameter {

    @Override //
    public Principal findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception {
        return req.getUserPrincipal();
    }
}
