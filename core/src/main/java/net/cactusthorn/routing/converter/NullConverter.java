package net.cactusthorn.routing.converter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NullConverter implements Converter<Object> {

    @Override //
    public Object convert(HttpServletRequest req, HttpServletResponse res, ServletContext con, String input) {
        return null;
    }
}
