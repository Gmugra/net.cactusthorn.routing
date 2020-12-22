package net.cactusthorn.routing.converter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Converter<T> {

    T convert(HttpServletRequest req, HttpServletResponse res, ServletContext con, String input);
}
