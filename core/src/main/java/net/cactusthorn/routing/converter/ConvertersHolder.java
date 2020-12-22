package net.cactusthorn.routing.converter;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ConvertersHolder {

    private static final Converter<?> NULL = new NullConverter();

    private final Map<Class<?>, Converter<?>> converters = new HashMap<>();

    private final Map<Class<?>, ValueOfConverter> valueOfconverters = new HashMap<>();

    public ConvertersHolder() {

        converters.put(HttpServletRequest.class, new HttpServletRequestConverter());
        converters.put(HttpServletResponse.class, new HttpServletResponseConverter());
        converters.put(HttpSession.class, new HttpSessionConverter());
        converters.put(ServletContext.class, new ServletContextConverter());

        converters.put(Byte.TYPE, new PrimitiveConverter(Byte.TYPE));
        converters.put(Short.TYPE, new PrimitiveConverter(Short.TYPE));
        converters.put(Integer.TYPE, new PrimitiveConverter(Integer.TYPE));
        converters.put(Long.TYPE, new PrimitiveConverter(Long.TYPE));
        converters.put(Float.TYPE, new PrimitiveConverter(Float.TYPE));
        converters.put(Double.TYPE, new PrimitiveConverter(Double.TYPE));
        converters.put(Character.TYPE, new PrimitiveConverter(Character.TYPE));
        converters.put(Boolean.TYPE, new PrimitiveConverter(Boolean.TYPE));

        converters.put(Integer.class, new IntegerConverter());
        converters.put(String.class, new StringConverter());
    }

    public Converter<?> findConverter(Class<?> clazz) {
        Converter<?> converter = converters.get(clazz);
        if (converter != null) {
            return converter;
        }
        converter = findValueOf(clazz);
        if (converter != null) {
            return converter;
        }
        return NULL;
    }

    private Converter<?> findValueOf(Class<?> clazz) {
        if (valueOfconverters.containsKey(clazz)) {
            ValueOfConverter converter = valueOfconverters.get(clazz);
            if (converter == null) {
                return null;
            }
            return converter;
        }
        if (ValueOfConverter.support(clazz)) {
            ValueOfConverter converter = new ValueOfConverter(clazz);
            valueOfconverters.put(clazz, converter);
            return converter;
        }
        valueOfconverters.put(clazz, null);
        return null;
    }

    public Converter<?> findConverter(Parameter parameter) {
        return findConverter(parameter.getType());
    }

    public <T> void register(Class<T> clazz, Converter<T> converter) {
        converters.put(clazz, converter);
    }
}
