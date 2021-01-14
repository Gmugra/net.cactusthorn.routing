package net.cactusthorn.routing.delegate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import javax.ws.rs.core.EntityTag;

import org.junit.jupiter.api.Test;

public class EntityTagHeaderDelegateTest {

    private final static EntityTagHeaderDelegate DELEGATE = new EntityTagHeaderDelegate();

    @Test //
    public void simple() {
        String expected = "\"xyzzy\"";
        EntityTag entityTag = DELEGATE.fromString(expected);
        assertEquals(expected, entityTag.toString());
    }

    @Test //
    public void empty() {
        String expected = "\"\"";
        EntityTag entityTag = DELEGATE.fromString(expected);
        assertEquals(expected, entityTag.toString());
    }

    @Test //
    public void weak() {
        String expected = "W/\"xyzzy\"";
        EntityTag entityTag = DELEGATE.fromString(expected);
        assertEquals(expected, entityTag.toString());
    }

    @Test //
    public void trim() {
        String expected = "   W/\"xyzzy\"  ";
        EntityTag entityTag = DELEGATE.fromString(expected);
        assertEquals(expected.trim(), entityTag.toString());
    }

    @Test //
    public void nullValue() {
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.toString(null));
        assertThrows(IllegalArgumentException.class, () -> DELEGATE.fromString(null));
    }
}
