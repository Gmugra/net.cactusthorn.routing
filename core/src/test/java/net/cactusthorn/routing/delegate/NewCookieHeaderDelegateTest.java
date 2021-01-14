package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.format.DateTimeParseException;

import javax.ws.rs.core.NewCookie;

import org.junit.jupiter.api.Test;

public class NewCookieHeaderDelegateTest {

    private static final NewCookieHeaderDelegate DELEGATE = new NewCookieHeaderDelegate();

    @Test public void simple() {
        String value = "id=a3fWa; Version=3; Max-Age=2592000; Comment=ABC; Domain = \"DOM AIN\"; Secure; HttpOnly;Path=PATH; Expires = Thu, 01 Dec 1994 16:00:00 GMT";
        String expected = "id=a3fWa;Version=3;Comment=ABC;Domain=\"DOM AIN\";Path=PATH;Max-Age=2592000;Secure;HttpOnly;Expires=Thu, 01 Dec 1994 16:00:00 GMT";
        NewCookie cookie = DELEGATE.fromString(value);
        assertEquals(expected, DELEGATE.toString(cookie));
    }

    @Test public void wrong() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString("aaaabbbb;Version=1"));
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString("aaaa=bbbb;Domain AAAA"));
        assertThrows(NumberFormatException.class, () -> DELEGATE.fromString("aaaa=bbbb;Version=B"));
        assertThrows(NumberFormatException.class, () -> DELEGATE.fromString("aaaa=bbbb;Max-Age=B"));
        assertThrows(DateTimeParseException.class, () -> DELEGATE.fromString("aaaa=bbbb;Expires=Thu 01 Dec 1994 16:00:00 GMT"));
    }

    @Test public void toNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.toString(null));
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString(null));
    }
}
