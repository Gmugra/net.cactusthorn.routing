package net.cactusthorn.routing.demo.jetty;

import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConverterException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateConverter implements Converter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    @Override //
    public LocalDate convert(Class<?> type, String value) throws ConverterException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(value, FORMATTER);
    }

}
