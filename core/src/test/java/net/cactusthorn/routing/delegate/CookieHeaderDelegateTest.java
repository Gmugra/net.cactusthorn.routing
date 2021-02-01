package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.ext.RuntimeDelegate;

import org.junit.jupiter.api.Test;

public class CookieHeaderDelegateTest {
    
    private final static RuntimeDelegate IMPL = new RuntimeDelegateImpl();

    private final static RuntimeDelegate.HeaderDelegate<Cookie> DELEGATE = IMPL.createHeaderDelegate(Cookie.class);

    @Test //
    public void full() {
        Cookie to = new Cookie("name", "some value", "PATH", "DOM AIN", 3);
        Cookie from = DELEGATE.fromString(DELEGATE.toString(to));
        assertEquals(to, from);
    }

    @Test //
    public void simple() {
        Cookie to = new Cookie("name", "some value");
        Cookie from = DELEGATE.fromString(DELEGATE.toString(to));
        assertEquals(to, from);
    }

    @Test //
    public void wrong() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString("$Version=1; aaaabbbb"));
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString("aaaa=bbbb;Domain AAAA"));
        assertThrows(NumberFormatException.class, () -> DELEGATE.fromString("$Version=B; aaaa=bbbb"));
    }

    @Test //
    public void toNull() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.toString(null));
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString(null));
    }
}
