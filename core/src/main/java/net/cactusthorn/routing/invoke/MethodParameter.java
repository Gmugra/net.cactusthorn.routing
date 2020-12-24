package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.annotation.Context;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public abstract class MethodParameter {

    protected Class<?> classType;
    protected String name;
    protected Class<?> converterType;
    protected Converter converter;

    MethodParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder, String contentType) {
        classType = parameter.getType();
    }

    abstract Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws ConverterException;

    protected final Class<?> converterType(Method method, Class<?> parameterType) {
        if (parameterType.isArray()) {
            Class<?> arrayType = parameterType.getComponentType();
            if (arrayType.isArray()) {
                throw new IllegalArgumentException("Multi-dimensional arrays are not supported; Wrong method: " + method.toGenericString());
            }
            return arrayType;
        }
        return parameterType;
    }

    static final class Factory {

        static final MethodParameter create(Method method, Parameter parameter, ConvertersHolder convertersHolder, String contentType) {
            Class<?> parameterClassType = parameter.getType();
            if (parameter.getAnnotation(PathParam.class) != null) {

                return new PathParamParameter(method, parameter, convertersHolder, contentType);
            } else if (parameter.getAnnotation(QueryParam.class) != null) {

                return new QueryParamParameter(method, parameter, convertersHolder, contentType);
            } else if (parameterClassType == HttpServletRequest.class) {

                return new HttpServletRequestParameter(method, parameter, convertersHolder, contentType);
            } else if (parameterClassType == HttpServletResponse.class) {

                return new HttpServletResponseParameter(method, parameter, convertersHolder, contentType);
            } else if (parameterClassType == HttpSession.class) {

                return new HttpSessionParameter(method, parameter, convertersHolder, contentType);
            } else if (parameterClassType == ServletContext.class) {

                return new ServletContextParameter(method, parameter, convertersHolder, contentType);
            } else if (parameter.getAnnotation(Context.class) != null) {

                return new BodyParameter(method, parameter, convertersHolder, contentType);
            }
            return new UnknownParameter(method, parameter, convertersHolder, contentType);
        }
    }
}
