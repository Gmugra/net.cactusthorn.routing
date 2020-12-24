package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public final class PathParamParameter extends MethodParameter {

    public PathParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder, String contentType) {
        super(method, parameter, convertersHolder, contentType);
        PathParam pathParam = parameter.getAnnotation(PathParam.class);
        name = pathParam.value();
        converterType = converterType(method, classType);
        if (converterType != classType) {
            throw new IllegalArgumentException("@PathParam can't be array or collection; Wrong method: " + method.toGenericString());
        }
        converter = convertersHolder.findConverter(converterType);
    }

    @Override //
    final Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws ConverterException {
        try {
            return converter.convert(converterType, requestData.pathValues().value(name));
        } catch (ConverterException ce) {
            throw ce;
        } catch (Exception e) {
            throw new ConverterException("Type Converting problem", e);
        }
    }
}
