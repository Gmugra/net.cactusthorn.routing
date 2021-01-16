package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.security.Principal;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PATCH;
import javax.ws.rs.core.Context;

import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.annotation.FormPart;

public abstract class MethodParameter {

    private Parameter parameter;
    private Class<?> classType;
    private String defaultValue;
    private String name;

    MethodParameter(Parameter parameter) {
        this.parameter = parameter;
        classType = parameter.getType();
        name = findName();
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

    protected Parameter parameter() {
        return parameter;
    }

    protected String findName() {
        return parameter.getName();
    }

    abstract Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) throws Exception;

    static final class Factory {

        protected static final String ONLY_POST_PUT_PATCH = "entity parameter supported only for POST, PUT and PATCH; Method: %s";

        static MethodParameter create(Method method, Parameter parameter, Type parameterGenericType, RoutingConfig routingConfig,
                Set<MediaType> consumesMediaTypes) {
            Class<?> parameterClassType = parameter.getType();
            if (parameter.getAnnotation(PathParam.class) != null) {
                return new PathParamParameter(method, parameter, routingConfig.convertersHolder());
            }
            if (parameter.getAnnotation(QueryParam.class) != null) {
                return new QueryParamParameter(method, parameter, routingConfig.convertersHolder());
            }
            if (parameter.getAnnotation(FormParam.class) != null) {
                return new FormParamParameter(method, parameter, routingConfig.convertersHolder(), consumesMediaTypes);
            }
            if (parameter.getAnnotation(FormPart.class) != null) {
                return new FormPartParameter(method, parameter);
            }
            if (parameter.getAnnotation(HeaderParam.class) != null) {
                return new HeaderParamParameter(method, parameter, routingConfig.convertersHolder());
            }
            if (parameter.getAnnotation(CookieParam.class) != null) {
                return new CookieParamParameter(method, parameter);
            }
            if (parameter.getAnnotation(Context.class) != null) {
                if (HttpServletRequest.class.isAssignableFrom(parameterClassType)) {
                    return new HttpServletRequestParameter(parameter);
                }
                if (HttpServletResponse.class.isAssignableFrom(parameterClassType)) {
                    return new HttpServletResponseParameter(parameter);
                }
                if (HttpSession.class.isAssignableFrom(parameterClassType)) {
                    return new HttpSessionParameter(parameter);
                }
                if (ServletContext.class.isAssignableFrom(parameterClassType)) {
                    return new ServletContextParameter(parameter);
                }
                if (Principal.class.isAssignableFrom(parameterClassType)) {
                    return new PrincipalParameter(parameter);
                }
            }
            if (method.getAnnotation(POST.class) != null || method.getAnnotation(PUT.class) != null
                    || method.getAnnotation(PATCH.class) != null) {
                return new BodyReaderParameter(method, parameter, parameterGenericType, consumesMediaTypes, routingConfig.bodyReaders());
            }
            throw new RoutingInitializationException(ONLY_POST_PUT_PATCH, method);
        }
    }
}
