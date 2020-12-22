package net.cactusthorn.routing.converter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpServletResponseConverter implements Converter<HttpServletResponse> {

    @Override //
    public HttpServletResponse convert(HttpServletRequest req, HttpServletResponse res, ServletContext con, String input) {
        return res;
    }
}
