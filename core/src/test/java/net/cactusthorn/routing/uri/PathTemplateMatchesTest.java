package net.cactusthorn.routing.uri;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.uri.PathTemplate.PathValues;

public class PathTemplateMatchesTest {

    @Test //
    public void simple() {
        PathTemplate t = new PathTemplate("/aaaa/dddd");
        assertTrue(t.match("/aaaa/dddd"));
    }

    @Test //
    public void spaces() {
        PathTemplate t = new PathTemplate("   /aaaa/dddd\n");
        assertTrue(t.match("/aaaa/dddd"));
    }

    @Test //
    public void simpleParam() {
        PathTemplate t = new PathTemplate("/aaa{var}bbb");
        assertTrue(t.match("/aaaXYZbbb)"));
        assertFalse(t.match("/aaabbb)"));
        assertTrue(t.match("/aaaXbbb)"));
    }

    @Test //
    public void regExpParam() {
        PathTemplate t = new PathTemplate("/aaa{ var: \\d{3} }bbb");
        assertTrue(t.match("/aaa123bbb)"));
        assertFalse(t.match("/aaa1234bbb)"));
        assertFalse(t.match("/aaaXYSbbb)"));
    }

    @Test //
    public void regExpCaseIgnore() {
        PathTemplate t = new PathTemplate("/{ var: (?i:address) }/something");
        assertTrue(t.match("/address/something"));
        assertTrue(t.match("/ADDRESS/something"));
        assertFalse(t.match("/addre/something"));
    }

    @Test //
    public void multiple() {
        PathTemplate t = new PathTemplate("/customer/{ id : \\d+ }/contact/{cid}/{var1}-{var2}");
        assertTrue(t.match("/customer/123/contact/XYZ/abc-def"));
    }

    @Test //
    public void values() {
        PathTemplate t = new PathTemplate("/customer/{ id : \\d{3} }/contact/{cid : [aBc]+}/{var1}-{var2}");
        PathTemplate.PathValues values = t.parse("/customer/303/contact/aaccB/WW-MMMM");
        assertEquals("303", values.value("id"));
        assertEquals("aaccB", values.value("cid"));
        assertEquals("WW", values.value("var1"));
        assertEquals("MMMM", values.value("var2"));
    }

    @Test //
    public void simpleValues() {
        PathTemplate t = new PathTemplate("/customer");
        PathTemplate.PathValues values = t.parse("/customer");
        assertEquals(PathValues.EMPTY, values);
    }

    @Test //
    public void noMatchSimple() {
        PathTemplate t = new PathTemplate("/customer/address");
        PathTemplate.PathValues values = t.parse("/customer");
        assertNull(values);
    }

    @Test //
    public void noMatch() {
        PathTemplate t = new PathTemplate("/customer/{var}");
        PathTemplate.PathValues values = t.parse("/customer");
        assertNull(values);
    }

    @Test //
    public void empty() {
        PathTemplate t = new PathTemplate("/customer/{var :  [abc]*}/doit");
        assertTrue(t.match("/customer//doit"));
        assertTrue(t.match("/customer/acccb/doit"));
        assertFalse(t.match("/customer/12/doit"));
    }
}
