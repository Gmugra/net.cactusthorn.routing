package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.CookieParam;
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
import net.cactusthorn.routing.annotation.FormPart;

class MethodParameterFactory {

    private static final String ONLY_POST_PUT_PATCH = "entity parameter supported only for POST, PUT and PATCH; Method: %s";
    private static final String CONTEXT_NOT_SUPPORTED = "@Context is not supporting %s; Method: %s";

    static List<MethodParameter> create(Method method, RoutingConfig routingConfig, Set<MediaType> consumesMediaTypes) {
        List<MethodParameter> parameters = new ArrayList<>();
        Parameter[] params = method.getParameters();
        Type[] types = method.getGenericParameterTypes();
        for (int i = 0; i < params.length; i++) {
            parameters.add(create(method, params[i], types[i], i, routingConfig, consumesMediaTypes));
        }
        return parameters;
    }

    static MethodParameter create(Method method, Parameter parameter, Type genericType, int position, RoutingConfig routingConfig,
            Set<MediaType> consumesMediaTypes) {

        if (parameter.getAnnotation(PathParam.class) != null) {
            return new PathParamParameter(method, parameter, genericType, position, routingConfig.convertersHolder());
        }
        if (parameter.getAnnotation(QueryParam.class) != null) {
            return new QueryParamParameter(method, parameter, genericType, position, routingConfig.convertersHolder());
        }
        if (parameter.getAnnotation(FormParam.class) != null) {
            return new FormParamParameter(method, parameter, genericType, position, routingConfig.convertersHolder(), consumesMediaTypes);
        }
        if (parameter.getAnnotation(FormPart.class) != null) {
            return new FormPartParameter(method, parameter, genericType, position, consumesMediaTypes);
        }
        if (parameter.getAnnotation(HeaderParam.class) != null) {
            return new HeaderParamParameter(method, parameter, genericType, position, routingConfig.convertersHolder());
        }
        if (parameter.getAnnotation(CookieParam.class) != null) {
            return new CookieParamParameter(method, parameter, genericType, position, routingConfig.convertersHolder());
        }
        if (parameter.getAnnotation(Context.class) != null) {
            if (HttpServletRequest.class == parameter.getType()) {
                return new HttpServletRequestParameter(method, parameter, genericType, position);
            }
            if (HttpServletResponse.class == parameter.getType()) {
                return new HttpServletResponseParameter(method, parameter, genericType, position);
            }
            if (ServletContext.class == parameter.getType()) {
                return new ServletContextParameter(method, parameter, genericType, position);
            }
            if (SecurityContext.class == parameter.getType()) {
                return new SecurityContextParameter(method, parameter, genericType, position);
            }
            throw new RoutingInitializationException(CONTEXT_NOT_SUPPORTED, parameter.getType(), method);
        }
        if (method.getAnnotation(POST.class) != null || method.getAnnotation(PUT.class) != null
                || method.getAnnotation(PATCH.class) != null) {
            return new BodyReaderParameter(method, parameter, genericType, position, consumesMediaTypes, routingConfig.bodyReaders());
        }
        throw new RoutingInitializationException(ONLY_POST_PUT_PATCH, method);
    }
}
