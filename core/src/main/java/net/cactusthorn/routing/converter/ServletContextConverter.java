package net.cactusthorn.routing.converter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ServletContextConverter implements Converter<ServletContext> {

    @Override //
    public ServletContext convert(HttpServletRequest req, HttpServletResponse res, ServletContext con, String input) {
        return con;
    }
}
