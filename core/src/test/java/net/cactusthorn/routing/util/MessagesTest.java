package net.cactusthorn.routing.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class MessagesTest {

    @Test public void isNull() {
        assertEquals("aa is null", Messages.isNull("aa"));
    }

    @Test public void isMissing() {
        assertEquals("Wrong: 'W' is missing", Messages.isMissing('W'));
    }

    @Test public void msg() {
        assertEquals("status code must be >= 100 and <= 599", Messages.msg(Messages.Key.WRONG_HTTP_STATUS_CODE));
    }

    @Test public void notExist() {
        assertEquals("aa not exist", Messages.notExist("aa"));
    }
}
