package net.cactusthorn.routing.invoke;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.PathTemplate.PathValues;

public final class HttpServletRequestParameter implements MethodParameter {

    @Override //
    public HttpServletRequest findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception {
        return req;
    }
}
