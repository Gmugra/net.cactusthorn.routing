package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import net.cactusthorn.routing.convert.ConvertersHolder;
import net.cactusthorn.routing.annotation.QueryParam;

public class QueryParamParameter extends MethodMultiValueParameter {

    public QueryParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder) {
        super(method, parameter, convertersHolder);
    }

    @Override //
    protected String findName(Parameter parameter) {
        String name = parameter.getAnnotation(QueryParam.class).value();
        if ("".equals(name)) {
            return super.findName(parameter);
        }
        return name;
    }
}
