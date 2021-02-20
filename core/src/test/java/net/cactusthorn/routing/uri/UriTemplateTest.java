package net.cactusthorn.routing.uri;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.uri.Template.TemplateVariable;

public class UriTemplateTest {

    @Test public void opaque() {
        UriTemplate uriTemplate = new UriTemplate("mail{var1}to:java-netÜ@java.sun.com");
        assertTrue(uriTemplate.opaque());
        assertEquals("mail{var1}to", uriTemplate.scheme());
        assertEquals("java-net%C3%9C@java.sun.com", uriTemplate.schemeSpecificPart());
        assertNull(uriTemplate.fragment());
    }

    @Test public void opaqueFragment() {
        UriTemplate uriTemplate = new UriTemplate("mail{var1}to:java-net@{ var2 : \\d{2} }#xy zw");
        assertTrue(uriTemplate.opaque());
        assertEquals("mail{var1}to", uriTemplate.scheme());
        assertEquals("java-net@{var2}", uriTemplate.schemeSpecificPart());
        assertEquals("xy%20zw", uriTemplate.fragment());
    }

    @Test public void hierarchical() {
        UriTemplate uriTemplate = new UriTemplate("http://java.sun.com/j2se/1.3/");
        assertFalse(uriTemplate.opaque());
        assertEquals("http", uriTemplate.scheme());
        assertNull(uriTemplate.userInfo());
        assertEquals("java.sun.com", uriTemplate.host());
        assertEquals("/j2se/1.3/", uriTemplate.path());
    }

    @Test public void hierarchicalUserInfo() {
        UriTemplate uriTemplate = new UriTemplate("http://aaü@java.sun.com/j2se/1.3/");
        assertFalse(uriTemplate.opaque());
        assertEquals("http", uriTemplate.scheme());
        assertEquals("aa%C3%BC", uriTemplate.userInfo());
        assertEquals("java.sun.com", uriTemplate.host());
        assertEquals("/j2se/1.3/", uriTemplate.path());
    }

    @Test public void hierarchicalQuery() {
        UriTemplate uriTemplate = new UriTemplate("//@:?aa=bb");
        assertFalse(uriTemplate.opaque());
        assertNull(uriTemplate.scheme());
        assertNull(uriTemplate.userInfo());
        assertNull(uriTemplate.host());
        assertNull(uriTemplate.port());
        assertEquals("@:", uriTemplate.authority());
        assertEquals("aa=bb", uriTemplate.query());
    }

    @Test public void hierarchicalPath() {
        UriTemplate uriTemplate = new UriTemplate("j2se/{var2:\\d{2}}");
        assertFalse(uriTemplate.opaque());
        assertNull(uriTemplate.scheme());
        assertNull(uriTemplate.userInfo());
        assertNull(uriTemplate.host());
        assertEquals("j2se/{var2}", uriTemplate.path());
        assertNull(uriTemplate.query());
    }

    @Test public void hierarchicalIP6() {
        UriTemplate uriTemplate = new UriTemplate("http://aaa@[2607:f0d0:1002:51::4]:{varPort}/");
        assertFalse(uriTemplate.opaque());
        assertEquals("aaa", uriTemplate.userInfo());
        assertEquals("[2607:f0d0:1002:51::4]", uriTemplate.host());
        assertEquals("{varPort}", uriTemplate.port());
        assertEquals("/", uriTemplate.path());
        assertNull(uriTemplate.query());
    }

    @Test public void hierarchicalIP4() {
        UriTemplate uriTemplate = new UriTemplate("http://{varIP4}:{varPort}/");
        assertFalse(uriTemplate.opaque());
        assertNull(uriTemplate.userInfo());
        assertEquals("{varIP4}", uriTemplate.host());
        assertEquals("{varPort}", uriTemplate.port());
        assertEquals("/", uriTemplate.path());
        assertNull(uriTemplate.query());
    }

    @Test public void hierarchicalFragment() {
        UriTemplate uriTemplate = new UriTemplate("{schema}://{user}@{varIP4}#xyz");
        assertFalse(uriTemplate.opaque());
        assertEquals("{schema}", uriTemplate.scheme());
        assertEquals("{user}", uriTemplate.userInfo());
        assertEquals("{varIP4}", uriTemplate.host());
        assertNull(uriTemplate.port());
        assertEquals("",uriTemplate.path());
        assertNull(uriTemplate.query());
        assertEquals("xyz", uriTemplate.fragment());
    }

    @Test public void templateVaribaleEqualsAnsHash() {
        UriTemplate uriTemplate = new UriTemplate("{schema:\\d{2}}://@/{schema:\\d{2}}");
        TemplateVariable var1 = uriTemplate.variables().get(0);
        TemplateVariable var2 = uriTemplate.variables().get(1);
        assertEquals(var1, var1);
        assertEquals(var1, var2);
        assertEquals(var1.hashCode(), var2.hashCode());
    }

    @Test public void templateVaribaleNotEquals() {
        UriTemplate uriTemplate = new UriTemplate("{schema:\\d{2}}://@/{path}");
        TemplateVariable var1 = uriTemplate.variables().get(0);
        TemplateVariable var2 = uriTemplate.variables().get(1);
        assertNotEquals(var1, 10);
        assertNotEquals(var1, var2);
    }
}
