package net.cactusthorn.routing.converter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpServletRequestConverter implements Converter<HttpServletRequest> {

    @Override //
    public HttpServletRequest convert(HttpServletRequest req, HttpServletResponse res, ServletContext con, String input) {
        return req;
    }

}
