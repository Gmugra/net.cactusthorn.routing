package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.format.DateTimeParseException;
import java.util.Date;

import org.junit.jupiter.api.Test;

public class DateHeaderDelegateTest {

    private static final DateHeaderDelegate DELEGATE = new DateHeaderDelegate();

    @Test
    public void simpleRFC_1123() {
        Date date = DELEGATE.fromString("Thu, 01 Dec 1994 16:00:00 GMT");
        assertNotNull(date);
    }

    @Test
    public void fromTrim() {
        Date date = DELEGATE.fromString("  Thu, 01 Dec 1994 16:00:00 GMT  ");
        assertNotNull(date);
    }

    @Test
    public void simpleRFC_1036() {
        Date date = DELEGATE.fromString("Tuesday, 06-Nov-01 08:49:37 GMT");
        assertNotNull(date);
    }

    @Test
    public void simpleANSI_C() {
        Date date = DELEGATE.fromString("Tue Nov 6 08:49:37 2001");
        assertNotNull(date);
    }

    @Test
    public void wrongFrom() {
        assertThrows(DateTimeParseException.class, () -> DELEGATE.fromString("Tue Nov 6 x 08:49:37 2001"));
    }

    @Test
    public void fromNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString(null));
    }

    @Test
    public void _toString() {
        String expected = "Thu, 01 Dec 1994 16:00:00 GMT";
        Date date = DELEGATE.fromString(expected);
        String asString = DELEGATE.toString(date);
        assertEquals(expected, asString);
    }

    @Test
    public void toNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.toString(null));
    }
}
