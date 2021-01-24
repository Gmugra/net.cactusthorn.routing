package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HeaderParam;

import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class HeaderParamParameter extends MethodSingleValueParameter {

    public HeaderParamParameter(Method method, Parameter parameter, Type parameterGenericType, ConvertersHolder convertersHolder) {
        super(method, parameter, parameterGenericType, convertersHolder);
    }

    @Override //
    protected String annotationName() {
        return HeaderParam.class.getSimpleName();
    }

    @Override //
    protected String findName() {
        String name = parameter().getAnnotation(HeaderParam.class).value();
        if ("".equals(name)) {
            return super.findName();
        }
        return name;
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) throws Exception {
        String value = req.getHeader(name());
        if (defaultValue() != null && value == null) {
            value = defaultValue();
        }
        return converter().convert(classType(), parameterGenericType(), parameter().getAnnotations(), value);
    }

}
