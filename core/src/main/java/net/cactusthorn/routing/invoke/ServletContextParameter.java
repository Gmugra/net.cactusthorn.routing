package net.cactusthorn.routing.invoke;

import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.convert.ConverterException;

public final class ServletContextParameter extends MethodParameter {

    public ServletContextParameter(Parameter parameter) {
        super(parameter);
    }

    @Override //
    ServletContext findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws ConverterException {
        return con;
    }

}
