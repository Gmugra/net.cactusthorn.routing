package net.cactusthorn.routing.converter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IntegerConverter implements Converter<Integer> {

    @Override //
    public Integer convert(HttpServletRequest req, HttpServletResponse res, ServletContext con, String input) {
        if (input == null) {
            return null;
        }
        return Integer.valueOf(input);
    }

}
