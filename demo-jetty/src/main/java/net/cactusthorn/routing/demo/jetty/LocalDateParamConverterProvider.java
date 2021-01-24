package net.cactusthorn.routing.demo.jetty;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

public class LocalDateParamConverterProvider implements ParamConverterProvider {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    private static final ParamConverter<LocalDate> CONVERTER = new ParamConverter<LocalDate>() {

        @Override //
        public LocalDate fromString(String value) {
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            return LocalDate.parse(value, FORMATTER);
        }

        @Override //
        public String toString(LocalDate value) {
            if (value == null) {
                return null;
            }
            return value.format(FORMATTER);
        }
    };

    @Override @SuppressWarnings("unchecked") //
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType == LocalDate.class) {
            return (ParamConverter<T>) CONVERTER;
        }
        return null;
    }
}
