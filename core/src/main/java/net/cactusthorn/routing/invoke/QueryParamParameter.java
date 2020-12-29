package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class QueryParamParameter extends MethodComplexParameter {

    private String name;
    private Class<?> converterType;
    private Converter converter;

    private boolean array;
    private boolean collection;
    private Class<?> collectionType;

    public QueryParamParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder) {
        super(parameter);
        name = initName(parameter);

        Optional<Class<?>> optionalArray = arrayType(method);
        if (optionalArray.isPresent()) {
            array = true;
            converterType = optionalArray.get();
            converter = findConverter(method, converterType, convertersHolder);
            return;
        }

        Optional<Class<?>> optionalCollection = collectionType();
        if (optionalCollection.isPresent()) {
            collection = true;
            collectionType = optionalCollection.get();
            converterType = collectionGenericType(method, parameter);
            converter = findConverter(method, converterType, convertersHolder);
            return;
        }

        converterType = classType();
        converter = findConverter(method, convertersHolder);
    }

    protected String initName(Parameter parameter) {
        QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
        return queryParam.value();
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, RequestData requestData) throws Exception {
        if (array) {
            return converter.convert(converterType, arrayValues(req));
        }
        if (collection) {
            return createCollection(collectionType, converterType, converter, arrayValues(req));
        }
        String value = req.getParameter(name);
        if (defaultValue() != null && value == null) {
            value = defaultValue();
        }
        return converter.convert(converterType, value);
    }

    private String[] arrayValues(HttpServletRequest req) {
        String[] values = req.getParameterValues(name);
        if (defaultValue() != null && values == null) {
            return new String[] {defaultValue()};
        }
        return values;
    }
}
