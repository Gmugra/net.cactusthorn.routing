package net.cactusthorn.routing.delegate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Date;
import java.util.Locale;

import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public final class DateHeaderDelegate implements HeaderDelegate<Date> {

    public static final String RFC_1123_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

    public static final String RFC_1036_FORMAT = "EEEE, dd-MMM-yy HH:mm:ss zzz";

    public static final String ANSI_C_FORMAT = "EEE MMM d HH:mm:ss yyyy";

    private static final ZoneId GMT = ZoneId.of("GMT");

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(RFC_1123_FORMAT, Locale.UK);

    private static final DateTimeFormatter PARSE_FORMATTER;
    static {
        // @formatter:off
        DateTimeFormatterBuilder builder =
            new DateTimeFormatterBuilder()
            .appendPattern('[' + RFC_1123_FORMAT + ']')
            .appendPattern('[' + RFC_1036_FORMAT + ']')
            .appendPattern('[' + ANSI_C_FORMAT + ']')
            .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.MICRO_OF_SECOND, 0)
            .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0);
        // @formatter:on
        PARSE_FORMATTER = builder.toFormatter(Locale.UK);
    }

    @Override //
    public Date fromString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value can not be null");
        }
        String tmp = value.trim();
        try {
            return Date.from(ZonedDateTime.parse(tmp, PARSE_FORMATTER).toInstant());
        } catch (DateTimeParseException e) {
            return Date.from(LocalDateTime.parse(tmp, PARSE_FORMATTER).atZone(GMT).toInstant());
        }
    }

    @Override //
    public String toString(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date can not be null");
        }
        return date.toInstant().atZone(GMT).format(FORMATTER);
    }
}
