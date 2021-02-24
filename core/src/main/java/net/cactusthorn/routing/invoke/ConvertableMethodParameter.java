package net.cactusthorn.routing.invoke;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.DefaultValue;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConvertersHolder;

import net.cactusthorn.routing.util.Messages;
import static net.cactusthorn.routing.util.Messages.Key.UNKNOWN_CONVERTER;

public abstract class ConvertableMethodParameter extends MethodParameter {

    private Object defaultObject;

    private Converter<?> converter;
    private Class<?> converterType;
    private Type converterGenericType;
    private Optional<Class<?>> collection = Optional.empty();

    public ConvertableMethodParameter(Method method, Parameter parameter, Type genericType, int position,
            ConvertersHolder convertersHolder) {

        super(method, parameter, genericType, position);

        collection = collectionType();

        converterType = parameter.getType();
        converterGenericType = genericType;
        if (collection.isPresent()) {
            Class<?> collectionGenericType = collectionGenericType();
            if (collectionGenericType != null) {
                converterType = collectionGenericType;
                converterGenericType = collectionGenericType;
            }
        }
        converter = findConverter(convertersHolder);

        String defautlValue = null;
        DefaultValue defaultAnnotation = annotation(DefaultValue.class);
        if (defaultAnnotation != null) {
            defautlValue = defaultAnnotation.value();
        }
        defaultObject = createDefaultObject(defautlValue);
    }

    protected Class<?> converterType() {
        return converterType;
    }

    protected Type converterGenericType() {
        return converterGenericType;
    }

    protected boolean collection() {
        return collection.isPresent();
    }

    protected Converter<?> findConverter(ConvertersHolder convertersHolder) {
        Optional<Converter<?>> optional = convertersHolder.findConverter(converterType(), converterGenericType(), annotations());
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new RoutingInitializationException(Messages.msg(UNKNOWN_CONVERTER, type(), method()));
        }
    }

    protected Object createDefaultObject(String defautlValue) {
        try {
            if (collection()) {
                if (defautlValue == null) {
                    return emptyCollection();
                }
                return createCollection(new String[] {defautlValue});
            }
            return converter.convert(converterType(), converterGenericType(), annotations(), defautlValue);
        } catch (Throwable e) {
            throw new RoutingInitializationException(Messages.msg(UNKNOWN_CONVERTER, method()), e);
        }
    }

    @SuppressWarnings("unchecked") //
    protected <T> T convert(String value) throws Throwable {
        if (value == null) {
            return (T) defaultObject;
        }
        return (T) converter.convert(converterType(), converterGenericType(), annotations(), value);
    }

    @SuppressWarnings("unchecked") //
    protected <T> Collection<T> convert(String[] values) throws Throwable {
        if (values == null) {
            return (Collection<T>) defaultObject;
        }
        return createCollection(values);
    }

    @SuppressWarnings("unchecked") //
    protected <T> Collection<T> convert(Enumeration<String> values) throws Throwable {
        if (values == null) {
            return (Collection<T>) defaultObject;
        }
        return createCollection(Collections.list(values).toArray(new String[0]));
    }

    /**
     * According to JSR-339 supported only List<T>, Set<T>, or SortedSet<T>
     */
    private Optional<Class<?>> collectionType() {
        if (List.class == type() || SortedSet.class == type() || Set.class == type()) {
            return Optional.of(type());
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

    private <T> Collection<T> createCollection(String[] values) throws Throwable {
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
        if (genericType() instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType();
            Type[] genericTypes = parameterizedType.getActualTypeArguments();
            return (Class<?>) genericTypes[0];
        }
        return null;
    }
}
