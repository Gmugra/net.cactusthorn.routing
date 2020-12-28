package net.cactusthorn.routing.demo.jetty;

import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.convert.Converter;
import net.cactusthorn.routing.convert.ConverterException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateConverter implements Converter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy");

    @Override //
    public LocalDate convert(RequestData requestData, Class<?> type, String value) throws ConverterException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value, FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ConverterException("LocalDate converting failed", e);
        }
    }

}
