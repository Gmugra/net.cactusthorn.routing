package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public final class HttpSessionParameter extends MethodParameter {

    public HttpSessionParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder, String contentType) {
        super(method, parameter, convertersHolder, contentType);
    }

    @Override //
    final HttpSession findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws ConverterException {
        return req.getSession(false);
    }
}
