package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import javax.ws.rs.QueryParam;

import net.cactusthorn.routing.convert.ConvertersHolder;

public class QueryParamParameter extends MethodMultiValueParameter {

    public QueryParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder) {
        super(method, parameter, convertersHolder);
    }

    @Override //
    protected String findName() {
        String name = parameter().getAnnotation(QueryParam.class).value();
        if ("".equals(name)) {
            return super.findName();
        }
        return name;
    }
}
