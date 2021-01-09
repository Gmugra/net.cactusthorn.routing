package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HeaderParam;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class HeaderParamParameter extends MethodSingleValueParameter {

    public HeaderParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder) {
        super(method, parameter, convertersHolder);
    }

    @Override //
    protected String annotationName() {
        return HeaderParam.class.getSimpleName();
    }

    @Override //
    protected String findName(Parameter parameter) {
        String name = parameter.getAnnotation(HeaderParam.class).value();
        if ("".equals(name)) {
            return super.findName(parameter);
        }
        return name;
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData) throws Exception {
        String value = req.getHeader(name());
        if (defaultValue() != null && value == null) {
            value = defaultValue();
        }
        return converter().convert(classType(), value);
    }

}
