package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.PathParam;

import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class PathParamParameter extends MethodSingleValueParameter {

    public PathParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder) {
        super(method, parameter, convertersHolder);
    }

    @Override //
    protected String annotationName() {
        return PathParam.class.getSimpleName();
    }

    @Override //
    protected String findName() {
        String name = parameter().getAnnotation(PathParam.class).value();
        if ("".equals(name)) {
            return super.findName();
        }
        return name;
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) throws Exception {
        String value = pathValues.value(name());
        if (defaultValue() != null && "".equals(value)) {
            value = defaultValue();
        }
        return converter().convert(classType(), value);
    }
}
