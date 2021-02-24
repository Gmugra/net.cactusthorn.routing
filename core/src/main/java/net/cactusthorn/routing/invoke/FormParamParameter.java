package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
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
import net.cactusthorn.routing.convert.ConvertersHolder;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;

import net.cactusthorn.routing.util.Messages;
import static net.cactusthorn.routing.util.Messages.Key.WRONG_CONTENT_TYPE;
import static net.cactusthorn.routing.util.Messages.Key.ERROR_AT_PARAMETER_POSITION;

public class FormParamParameter extends ConvertableMethodParameter {

    // @formatter:off
    private static final Set<MediaType> CONTENT_TYPE = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    new MediaType[] {MediaType.APPLICATION_FORM_URLENCODED_TYPE, MediaType.MULTIPART_FORM_DATA_TYPE})));
    // @formatter:off

    public FormParamParameter(Method method, Parameter parameter, Type genericType, int position, ConvertersHolder convertersHolder,
            Set<MediaType> consumesMediaTypes) {
        super(method, parameter, genericType, position, convertersHolder);

        for (MediaType contentType : consumesMediaTypes) {
            if (!CONTENT_TYPE.contains(contentType)) {
                throw new RoutingInitializationException(
                        Messages.msg(WRONG_CONTENT_TYPE, "@FormParam", CONTENT_TYPE, method()));
            }
        }
    }

    @Override //
    public String name() {
        String name = annotation(FormParam.class).value();
        if ("".equals(name)) {
            return super.name();
        }
        return name;
    }

    @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {
        try {
            if (collection()) {
                return convert(req.getParameterValues(name()));
            }
            return convert(req.getParameter(name()));
        } catch (Throwable e) {
            throw new NotFoundException(Messages.msg(ERROR_AT_PARAMETER_POSITION, position(), type().getSimpleName(), e), e);
        }
    }
}
