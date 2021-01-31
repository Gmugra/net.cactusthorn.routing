package net.cactusthorn.routing.invoke;

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
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.annotation.FormPart;

public interface MethodParameter {

    String CONVERSION_ERROR_MESSAGE = "Parameter position: %s; Parameter type: %s; %s";

    default String name() {
        return null;
    }

    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) throws Exception;

    class Factory {

        protected static final String ONLY_POST_PUT_PATCH = "entity parameter supported only for POST, PUT and PATCH; Method: %s";

        static MethodParameter create(ParameterInfo parameterInfo, RoutingConfig routingConfig, Set<MediaType> consumesMediaTypes) {
            if (parameterInfo.annotation(PathParam.class) != null) {
                return new PathParamParameter(parameterInfo);
            }
            if (parameterInfo.annotation(QueryParam.class) != null) {
                return new QueryParamParameter(parameterInfo);
            }
            if (parameterInfo.annotation(FormParam.class) != null) {
                return new FormParamParameter(parameterInfo, consumesMediaTypes);
            }
            if (parameterInfo.annotation(FormPart.class) != null) {
                return new FormPartParameter(parameterInfo);
            }
            if (parameterInfo.annotation(HeaderParam.class) != null) {
                return new HeaderParamParameter(parameterInfo);
            }
            if (parameterInfo.annotation(CookieParam.class) != null) {
                return new CookieParamParameter(parameterInfo);
            }
            if (parameterInfo.annotation(Context.class) != null) {
                if (HttpServletRequest.class.isAssignableFrom(parameterInfo.type())) {
                    return new HttpServletRequestParameter();
                }
                if (HttpServletResponse.class.isAssignableFrom(parameterInfo.type())) {
                    return new HttpServletResponseParameter();
                }
                if (ServletContext.class.isAssignableFrom(parameterInfo.type())) {
                    return new ServletContextParameter();
                }
                if (SecurityContext.class == parameterInfo.type()) {
                    return new SecurityContextParameter();
                }
            }
            if (parameterInfo.method().getAnnotation(POST.class) != null || parameterInfo.method().getAnnotation(PUT.class) != null
                    || parameterInfo.method().getAnnotation(PATCH.class) != null) {
                return new BodyReaderParameter(parameterInfo, consumesMediaTypes, routingConfig.bodyReaders());
            }
            throw new RoutingInitializationException(ONLY_POST_PUT_PATCH, parameterInfo.method());
        }
    }
}
