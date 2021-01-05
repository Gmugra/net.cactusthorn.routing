package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.annotation.Context;
import net.cactusthorn.routing.annotation.CookieParam;
import net.cactusthorn.routing.annotation.FormParam;
import net.cactusthorn.routing.annotation.FormPart;
import net.cactusthorn.routing.annotation.HeaderParam;
import net.cactusthorn.routing.annotation.PathParam;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.annotation.DefaultValue;
import net.cactusthorn.routing.convert.ConvertersHolder;

public abstract class MethodParameter {

    private Class<?> classType;
    private String defaultValue;
    private String name;

    MethodParameter(Parameter parameter) {
        classType = parameter.getType();
        name = findName(parameter);
        DefaultValue defaultValueAnnotation = parameter.getAnnotation(DefaultValue.class);
        if (defaultValueAnnotation != null) {
            defaultValue = defaultValueAnnotation.value();
        }
    }

    protected String defaultValue() {
        return defaultValue;
    }

    protected Class<?> classType() {
        return classType;
    }

    protected String name() {
        return name;
    }

    protected String findName(Parameter parameter) {
        return parameter.getName();
    }

    abstract Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData)
            throws Exception;

    static final class Factory {

        static MethodParameter create(Method method, Parameter parameter, ConvertersHolder convertersHolder, String contentType) {
            Class<?> parameterClassType = parameter.getType();
            if (parameter.getAnnotation(PathParam.class) != null) {
                return new PathParamParameter(method, parameter, convertersHolder);
            }
            if (parameter.getAnnotation(QueryParam.class) != null) {
                return new QueryParamParameter(method, parameter, convertersHolder);
            }
            if (parameter.getAnnotation(FormParam.class) != null) {
                return new FormParamParameter(method, parameter, convertersHolder, contentType);
            }
            if (parameter.getAnnotation(FormPart.class) != null) {
                return new FormPartParameter(method, parameter);
            }
            if (parameter.getAnnotation(HeaderParam.class) != null) {
                return new HeaderParamParameter(method, parameter, convertersHolder);
            }
            if (parameter.getAnnotation(CookieParam.class) != null) {
                return new CookieParamParameter(method, parameter);
            }
            if (parameterClassType == HttpServletRequest.class) {
                return new HttpServletRequestParameter(parameter);
            }
            if (parameterClassType == HttpServletResponse.class) {
                return new HttpServletResponseParameter(parameter);
            }
            if (parameterClassType == HttpSession.class) {
                return new HttpSessionParameter(parameter);
            }
            if (parameterClassType == ServletContext.class) {
                return new ServletContextParameter(parameter);
            }
            if (parameterClassType == Principal.class) {
                return new PrincipalParameter(parameter);
            }
            if (parameter.getAnnotation(Context.class) != null) {
                return new BodyParameter(method, parameter, convertersHolder, contentType);
            }
            return new UnknownParameter(parameter);
        }
    }
}
