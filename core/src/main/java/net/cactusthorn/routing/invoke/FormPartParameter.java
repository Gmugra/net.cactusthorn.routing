package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.ws.rs.core.MediaType;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.annotation.FormPart;

public class FormPartParameter extends MethodParameter {

    private static final String WRONG_TYPE = "@FormPart can be used only for javax.servlet.http.Part type; Method: %s";
    private static final String WRONG_CONTENT_TYPE = "@FormPart can be used only with @Consumes content types: %s; Method: %s";

    // @formatter:off
    private static final Set<MediaType> CONTENT_TYPE = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(new MediaType[] {MediaType.MULTIPART_FORM_DATA_TYPE})));
    // @formatter:off

    public FormPartParameter(Method method, Parameter parameter, Type genericType, int position, Set<MediaType> consumesMediaTypes) {
        super(method, parameter, genericType, position);

        for (MediaType contentType : consumesMediaTypes) {
            if (!CONTENT_TYPE.contains(contentType)) {
                throw new RoutingInitializationException(WRONG_CONTENT_TYPE, CONTENT_TYPE, method());
            }
        }

        if (type() != Part.class) {
            throw new RoutingInitializationException(WRONG_TYPE, method());
        }
    }

    @Override //
    public String name() {
        String name = annotation(FormPart.class).value();
        if ("".equals(name)) {
            return super.name();
        }
        return name;
    }

    @Override //
    public Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) throws Exception {
        Collection<Part> parts = req.getParts();
        if (parts == null) {
            return null;
        }
        for (Part part : req.getParts()) {
            if (name().equals(part.getName())) {
                return part;
            }
        }
        return null;
    }
}
