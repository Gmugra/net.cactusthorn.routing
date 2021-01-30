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

import net.cactusthorn.routing.annotation.FormPart;

import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class ParameterInfo {

    protected static final String CANT_BE_COLLECTION_MESSAGE = "%s can't be collection; Method: %s";
    protected static final String UNKNOWN_CONVERTER_MESSAGE = "Converter for %s unknown; Method: %s";

    private Method method;
    private Parameter parameter;
    private Type genericType;
    private String defaultValue;
    private String[] defaultValues;
    private String name;
    private int position;

    private Converter<?> converter;
    private Class<?> coverterType;
    private Type coverterGenericType;
    private Optional<Class<?>> collection = Optional.empty();

    public ParameterInfo(Method method, Parameter parameter, Type genericType, int position, ConvertersHolder convertersHolder) {

        this.method = method;
        this.parameter = parameter;
        this.genericType = genericType;
        this.position = position + 1;
        name = parameter.getName();

        boolean paramAnnotation = paramAnnotation();

        if (parameter.getAnnotation(Context.class) != null || parameter.getAnnotation(FormPart.class) != null
                || parameter.getAnnotation(CookieParam.class) != null || !paramAnnotation) {
            return;
        }

        DefaultValue defaultValueAnnotation = parameter.getAnnotation(DefaultValue.class);
        if (defaultValueAnnotation != null) {
            defaultValue = defaultValueAnnotation.value();
            defaultValues = new String[] {defaultValue};
        }

        collection = collectionType();

        if (collection.isPresent()) {
            if (annotation(PathParam.class) != null) {
                throw new RoutingInitializationException(CANT_BE_COLLECTION_MESSAGE, PathParam.class.getSimpleName(), method);
            }
            if (annotation(HeaderParam.class) != null) {
                throw new RoutingInitializationException(CANT_BE_COLLECTION_MESSAGE, HeaderParam.class.getSimpleName(), method);
            }
        }

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
    }

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
        return name;
    }

    protected int position() {
        return position;
    }

    protected boolean collection() {
        return collection.isPresent();
    }

    @SuppressWarnings("unchecked") //
    protected <T> T convert(String value) throws Exception {
        String prepared = applyDefault(value);
        return (T) converter.convert(coverterType, coverterGenericType, annotations(), prepared);
    }

    protected <T> Collection<T> convert(String[] values) throws Exception {
        String[] prepared = applyDefault(values);
        if (prepared == null || prepared.length == 0) {
            return emptyCollection();
        }
        return createCollection(prepared);
    }

    private boolean paramAnnotation() {
        return annotation(QueryParam.class) != null || annotation(FormParam.class) != null || annotation(HeaderParam.class) != null
                || annotation(PathParam.class) != null;
    }

    private String applyDefault(String value) {
        if (defaultValue != null && value == null) {
            return defaultValue;
        }
        return value;
    }

    private String[] applyDefault(String[] values) {
        if (defaultValues != null && values == null) {
            return defaultValues;
        }
        return values;
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

    private <T> Collection<T> createCollection(String[] values) throws Exception {
        if (List.class == collection.get()) {
            List<T> result = new ArrayList<>();
            for (String value : values) {
                result.add(convert(value));
            }
            return Collections.unmodifiableList(result);
        }
        if (SortedSet.class == collection.get()) {
            SortedSet<T> result = new TreeSet<>();
            for (String value : values) {
                result.add(convert(value));
            }
            return Collections.unmodifiableSortedSet(result);
        }
        Set<T> result = new HashSet<>();
        for (String value : values) {
            result.add(convert(value));
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
