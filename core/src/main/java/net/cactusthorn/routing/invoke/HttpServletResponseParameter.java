package net.cactusthorn.routing.invoke;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.PathTemplate.PathValues;

public final class HttpServletResponseParameter implements MethodParameter {

    @Override //
    public HttpServletResponse findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception {
        return res;
    }
}
