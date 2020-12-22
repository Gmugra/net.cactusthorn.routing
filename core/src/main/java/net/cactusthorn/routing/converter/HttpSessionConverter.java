package net.cactusthorn.routing.converter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class HttpSessionConverter implements Converter<HttpSession> {

    @Override //
    public HttpSession convert(HttpServletRequest req, HttpServletResponse res, ServletContext con, String input) {
        return req.getSession(false);
    }

}
