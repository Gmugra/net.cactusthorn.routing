package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConvertersHolder;

public abstract class MethodMultiValueParameter extends MethodComplexParameter {

    private Class<?> converterType;
    private Converter converter;

    private boolean array;
    private boolean collection;
    private Class<?> collectionType;

    public MethodMultiValueParameter(Method method, Parameter parameter, ConvertersHolder convertersHolder) {
        super(parameter);

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

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) throws Exception {
        if (array) {
            return converter.convert(converterType, arrayValues(req));
        }
        if (collection) {
            return createCollection(collectionType, converterType, converter, arrayValues(req));
        }
        String value = req.getParameter(name());
        if (defaultValue() != null && value == null) {
            value = defaultValue();
        }
        return converter.convert(converterType, value);
    }

    private String[] arrayValues(HttpServletRequest req) {
        String[] values = req.getParameterValues(name());
        if (defaultValue() != null && values == null) {
            return new String[] {defaultValue()};
        }
        return values;
    }
}
