package net.cactusthorn.routing.invoke;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MediaType;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;

public class FormParamParameter implements MethodParameter {

    // @formatter:off
    private static final Set<MediaType> CONTENT_TYPE = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    new MediaType[] {MediaType.APPLICATION_FORM_URLENCODED_TYPE, MediaType.MULTIPART_FORM_DATA_TYPE})));
    // @formatter:off

    private static final String WRONG_CONTENT_TYPE = "@FormParam can be used only with @Consumes content types: %s; Method: %s";

    private ParameterInfo paramInfo;
    private String name;

    public FormParamParameter(ParameterInfo paramInfo, Set<MediaType> consumesMediaTypes) {
        this.paramInfo = paramInfo;

        for (MediaType contentType : consumesMediaTypes) {
            if (!CONTENT_TYPE.contains(contentType)) {
                throw new RoutingInitializationException(WRONG_CONTENT_TYPE, CONTENT_TYPE, paramInfo.method());
            }
        }

        name = paramInfo.annotation(FormParam.class).value();
        if ("".equals(name)) {
            name = paramInfo.name();
        }
    }

    @Override //
    public String name() {
        return name;
    }

    @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {
        try {
            if (paramInfo.collection()) {
                return paramInfo.convert(req.getParameterValues(name()));
            }
            return paramInfo.convert(req.getParameter(name()));
        } catch (Exception e) {
            throw new NotFoundException(
                    String.format(CONVERSION_ERROR_MESSAGE, paramInfo.position(), paramInfo.type().getSimpleName(), e), e);
        }
    }
}
