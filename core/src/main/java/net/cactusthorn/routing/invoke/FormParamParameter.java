package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class FormParamParameter extends MethodMultiValueParameter {

    // @formatter:off
    public static final Set<MediaType> CONTENT_TYPE = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    new MediaType[] {MediaType.APPLICATION_FORM_URLENCODED_TYPE, MediaType.MULTIPART_FORM_DATA_TYPE})));
    // @formatter:off

    protected static final String WRONG_CONTENT_TYPE = "@FormParam can be used only with @Consumes content types: %s; Method: %s";

    public FormParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder, Set<MediaType> consumesMediaTypes) {
        super(method, parameter, convertersHolder);
        for (MediaType contentType : consumesMediaTypes) {
            if (!CONTENT_TYPE.contains(contentType)) {
                throw new RoutingInitializationException(WRONG_CONTENT_TYPE, CONTENT_TYPE, method);
            }
        }
    }

    @Override //
    protected String findName(Parameter parameter) {
        String name = parameter.getAnnotation(FormParam.class).value();
        if ("".equals(name)) {
            return super.findName(parameter);
        }
        return name;
    }
}
