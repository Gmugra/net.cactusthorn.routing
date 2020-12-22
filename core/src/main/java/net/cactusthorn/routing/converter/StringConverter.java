package net.cactusthorn.routing.converter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StringConverter implements Converter<String> {

    @Override //
    public String convert(HttpServletRequest req, HttpServletResponse res, ServletContext con, String input) {
        return input;
    }

}
