package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.FormParam;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class FormParamParameter extends QueryParamParameter {

    // @formatter:off
    public static final Set<String> CONTENT_TYPE = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(new String[] {"application/x-www-form-urlencoded"})));
    // @formatter:off

    protected static final String WRONG_CONTENT_TYPE = "@FormParam can be used only with @Consumes content types: %s; Method: %s";

    public FormParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder, String contentType) {
        super(method, parameter, convertersHolder);
        if (!CONTENT_TYPE.contains(contentType)) {
            throw new RoutingInitializationException(WRONG_CONTENT_TYPE, CONTENT_TYPE, method);
        }
    }

    @Override //
    protected String initName(Parameter parameter) {
        FormParam formParam = parameter.getAnnotation(FormParam.class);
        return formParam.value();
    }
}
