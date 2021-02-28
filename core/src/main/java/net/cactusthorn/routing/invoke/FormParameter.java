package net.cactusthorn.routing.invoke;

import static net.cactusthorn.routing.util.Messages.Key.WRONG_CONTENT_TYPE;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.uri.PathTemplate.PathValues;
import net.cactusthorn.routing.util.Messages;

public class FormParameter extends MethodParameter {

    // @formatter:off
    private static final Set<MediaType> CONTENT_TYPE = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    new MediaType[] {MediaType.APPLICATION_FORM_URLENCODED_TYPE, MediaType.MULTIPART_FORM_DATA_TYPE})));
    // @formatter:off

    public FormParameter(Method method, Parameter parameter, Type genericType, int position, Set<MediaType> consumesMediaTypes) {
        super(method, parameter, genericType, position);

        for (MediaType contentType : consumesMediaTypes) {
            if (!CONTENT_TYPE.contains(contentType)) {
                throw new RoutingInitializationException(
                        Messages.msg(WRONG_CONTENT_TYPE, "javax.ws.rs.core.Form", CONTENT_TYPE, method()));
            }
        }
    }

    @Override //
    protected Form findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception {

        MultivaluedMap<String, String> parameters = new MultivaluedHashMap<>();
        for (Enumeration<String> e = req.getParameterNames(); e.hasMoreElements();) {
            String name = e.nextElement();
            String[] values = req.getParameterValues(name);
            parameters.addAll(name, values);
        }
        return new Form(parameters);
    }

}
