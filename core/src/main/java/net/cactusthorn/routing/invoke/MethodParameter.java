package net.cactusthorn.routing.invoke;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DefaultValue;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.PathTemplate.PathValues;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConvertersHolder;

public abstract class MethodParameter {

    protected static final String CANT_BE_COLLECTION_MESSAGE = "%s can't be collection; Method: %s";
    protected static final String UNKNOWN_CONVERTER_MESSAGE = "Converter for %s unknown; Method: %s";
    protected static final String CONVERSION_ERROR_MESSAGE = "Parameter position: %s; Parameter type: %s; %s";
    protected static final String WRONG_DEFAULT_MESSAGE = "Wrong Default Value; Method: %s";

    private Method method;
    private Parameter parameter;
    private Type genericType;
    private int position;

    private Object defaultValue;

    private Converter<?> converter;
    private Class<?> coverterType;
    private Type coverterGenericType;
    private Optional<Class<?>> collection = Optional.empty();

    public MethodParameter(Method method, Parameter parameter, Type genericType, int position) {
        this.method = method;
        this.parameter = parameter;
        this.genericType = genericType;
        this.position = position;
    }

    public MethodParameter(Method method, Parameter parameter, Type genericType, int position, ConvertersHolder convertersHolder) {

        this(method, parameter, genericType, position);

        collection = collectionType();

        coverterType = parameter.getType();
        coverterGenericType = genericType;
        if (collection.isPresent()) {
            Class<?> collectionGenericType = collectionGenericType();
            if (collectionGenericType != null) {
                coverterType = collectionGenericType;
                coverterGenericType = collectionGenericType;
            }
        }

        Optional<Converter<?>> optional = convertersHolder.findConverter(coverterType, coverterGenericType, parameter.getAnnotations());
        if (optional.isPresent()) {
            converter = optional.get();
        } else {
            throw new RoutingInitializationException(UNKNOWN_CONVERTER_MESSAGE, parameter.getType(), method);
        }

        defaultValue = createDefaultValue();
    }

    protected Object createDefaultValue() {
        DefaultValue annotation = annotation(DefaultValue.class);
        try {
            if (annotation == null) {
                if (collection()) {
                    return emptyCollection();
                }
                return converter.convert(coverterType, coverterGenericType, annotations(), (String) null);
            }
            if (collection()) {
                return createCollection(new String[] {annotation.value()});
            }
            return converter.convert(coverterType, coverterGenericType, annotations(), annotation.value());
        } catch (Exception e) {
            throw new RoutingInitializationException(WRONG_DEFAULT_MESSAGE, e, method);
        }
    }

    protected abstract Object findValue(HttpServletRequest req, HttpServletResponse res, ServletContext con, PathValues pathValues)
            throws Exception;

    protected Method method() {
        return method;
    }

    protected Type genericType() {
        return genericType;
    }

    protected Class<?> type() {
        return parameter.getType();
    }

    protected Annotation[] annotations() {
        return parameter.getAnnotations();
    }

    protected <T extends Annotation> T annotation(Class<T> annotationClass) {
        return parameter.getAnnotation(annotationClass);
    }

    protected String name() {
        return parameter.getName();
    }

    protected int position() {
        return position;
    }

    protected boolean collection() {
        return collection.isPresent();
    }

    @SuppressWarnings("unchecked") //
    protected <T> T convert(String value) throws Exception {
        if (value == null) {
            return (T) defaultValue;
        }
        return (T) converter.convert(coverterType, coverterGenericType, annotations(), value);
    }

    @SuppressWarnings("unchecked") //
    protected <T> Collection<T> convert(String[] values) throws Exception {
        if (values == null) {
            return (Collection<T>) defaultValue;
        }
        return createCollection(values);
    }

    /**
     * According to JSR-339 supported only List<T>, Set<T>, or SortedSet<T>
     */
    private Optional<Class<?>> collectionType() {
        if (List.class == parameter.getType() || SortedSet.class == parameter.getType() || Set.class == parameter.getType()) {
            return Optional.of(parameter.getType());
        }
        return Optional.empty();
    }

    private <T> Collection<T> emptyCollection() {
        if (List.class == collection.get()) {
            return Collections.emptyList();
        }
        if (SortedSet.class == collection.get()) {
            return Collections.emptySortedSet();
        }
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked") //
    private <T> Collection<T> createCollection(String[] values) throws Exception {
        if (List.class == collection.get()) {
            List<T> result = new ArrayList<>();
            for (String value : values) {
                result.add((T) converter.convert(coverterType, coverterGenericType, annotations(), value));
            }
            return Collections.unmodifiableList(result);
        }
        if (SortedSet.class == collection.get()) {
            SortedSet<T> result = new TreeSet<>();
            for (String value : values) {
                result.add((T) converter.convert(coverterType, coverterGenericType, annotations(), value));
            }
            return Collections.unmodifiableSortedSet(result);
        }
        Set<T> result = new HashSet<>();
        for (String value : values) {
            result.add((T) converter.convert(coverterType, coverterGenericType, annotations(), value));
        }
        return Collections.unmodifiableSet(result);
    }

    private Class<?> collectionGenericType() {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] genericTypes = parameterizedType.getActualTypeArguments();
            return (Class<?>) genericTypes[0];
        }
        return null;
    }
}
