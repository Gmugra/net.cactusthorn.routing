package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Optional;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.NotFoundException;

import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.convert.ConvertersHolder;

public abstract class MethodMultiValueParameter extends MethodComplexParameter {

    private boolean collection;
    private Class<?> collectionType;

    public MethodMultiValueParameter(Method method, Parameter parameter, Type parameterGenericType, ConvertersHolder convertersHolder) {
        super(method, parameter, parameterGenericType, convertersHolder);

        Optional<Class<?>> optionalCollection = collectionType();
        if (optionalCollection.isPresent()) {
            collection = true;
            collectionType = optionalCollection.get();
            return;
        }
    }

    @Override //
    Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues) {
        try {
            if (collection) {
                return createCollection(collectionType, arrayValues(req));
            }
            String value = req.getParameter(name());
            if (defaultValue() != null && value == null) {
                value = defaultValue();
            }
            return converter().convert(classType(), parameterGenericType(), parameter().getAnnotations(), value);
        } catch (Exception e) {
            throw new NotFoundException(e.getMessage(), e);
        }
    }

    private String[] arrayValues(HttpServletRequest req) {
        String[] values = req.getParameterValues(name());
        if (defaultValue() != null && values == null) {
            return new String[] {defaultValue()};
        }
        return values;
    }
}
