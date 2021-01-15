package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TemplatedTest {

    @Test //
    public void test() {
        Templated t = new Templated(null, null, "t", "o");
        t.request();
        t.response();
        assertEquals("t", t.template());
        assertEquals("o", t.entity());
    }
}
