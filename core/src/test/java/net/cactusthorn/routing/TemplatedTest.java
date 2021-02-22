package net.cactusthorn.routing;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.body.writer.Templated;

public class TemplatedTest {

    @Test //
    public void test() {
        Templated t = new Templated("t", "o", null, null);
        t.request();
        t.response();
        assertEquals("t", t.template());
        assertEquals("o", t.entity());
    }

    @Test //
    public void test2() {
        Templated t = new Templated("t", "o");
        assertEquals("t", t.template());
        assertEquals("o", t.entity());
    }

    @Test //
    public void test3() {
        Templated t = new Templated("t");
        assertEquals("t", t.template());
        assertNull(t.entity());
    }

    @Test //
    public void testException() {
        assertThrows(IllegalArgumentException.class, () -> new Templated(null));
    }
}
