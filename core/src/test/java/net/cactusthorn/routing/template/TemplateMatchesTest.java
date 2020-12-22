package net.cactusthorn.routing.template;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.Template;
import net.cactusthorn.routing.Template.PathValues;

public class TemplateMatchesTest {

    @Test //
    public void simple() {
        Template t = new Template("/aaaa/dddd");
        assertTrue(t.match("/aaaa/dddd"));
    }

    @Test //
    public void spaces() {
        Template t = new Template("   /aaaa/dddd\n");
        assertTrue(t.match("/aaaa/dddd"));
    }

    @Test //
    public void simpleParam() {
        Template t = new Template("/aaa{var}bbb");
        assertTrue(t.match("/aaaXYZbbb)"));
        assertFalse(t.match("/aaabbb)"));
        assertTrue(t.match("/aaaXbbb)"));
    }

    @Test //
    public void regExpParam() {
        Template t = new Template("/aaa{ var: \\d{3} }bbb");
        assertTrue(t.match("/aaa123bbb)"));
        assertFalse(t.match("/aaa1234bbb)"));
        assertFalse(t.match("/aaaXYSbbb)"));
    }

    @Test //
    public void regExpCaseIgnore() {
        Template t = new Template("/{ var: (?i:address) }/something");
        assertTrue(t.match("/address/something"));
        assertTrue(t.match("/ADDRESS/something"));
        assertFalse(t.match("/addre/something"));
    }

    @Test //
    public void multiple() {
        Template t = new Template("/customer/{ id : \\d+ }/contact/{cid}/{var1}-{var2}");
        assertTrue(t.match("/customer/123/contact/XYZ/abc-def"));
    }

    @Test //
    public void values() {
        Template t = new Template("/customer/{ id : \\d{3} }/contact/{cid : [aBc]+}/{var1}-{var2}");
        Template.PathValues values = t.parse("/customer/303/contact/aaccB/WW-MMMM");
        assertEquals("303", values.value("id"));
        assertEquals("aaccB", values.value("cid"));
        assertEquals("WW", values.value("var1"));
        assertEquals("MMMM", values.value("var2"));
    }

    @Test //
    public void simpleValues() {
        Template t = new Template("/customer");
        Template.PathValues values = t.parse("/customer");
        assertEquals(PathValues.EMPTY, values);
    }

    @Test //
    public void noMatchSimple() {
        Template t = new Template("/customer/address");
        Template.PathValues values = t.parse("/customer");
        assertNull(values);
    }

    @Test //
    public void noMatch() {
        Template t = new Template("/customer/{var}");
        Template.PathValues values = t.parse("/customer");
        assertNull(values);
    }
}
