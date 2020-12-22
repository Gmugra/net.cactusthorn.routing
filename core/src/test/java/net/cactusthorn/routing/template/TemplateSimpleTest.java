package net.cactusthorn.routing.template;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.Template;

public class TemplateSimpleTest {

    @Test //
    public void simple() {
        Template pt = new Template("/api/test/something");
        assertEquals(19, pt.literalCharsAmount());
        assertTrue(pt.isSimple());
    }

    @Test //
    public void nullTemplate() {
        assertThrows(IllegalArgumentException.class, () -> new Template(null));
    }

    @Test //
    public void emptyTemplate() {
        assertThrows(IllegalArgumentException.class, () -> new Template("\t"));
    }

    @Test //
    public void withSpaces() {
        Template pt = new Template("   /api/test/something\t");
        assertEquals(19, pt.literalCharsAmount());
        assertTrue(pt.isSimple());
    }

    @Test //
    public void withSimpleParams() {
        Template pt = new Template("/api/te{id}st/som{ddd}ething{ so_me }");
        assertEquals(19, pt.literalCharsAmount());
        assertFalse(pt.isSimple());
    }

    @Test //
    public void withParams() {
        Template pt = new Template("/api/te{id : xxxx}st/som{ddd}ething{some: xxx}");
        assertEquals(19, pt.literalCharsAmount());
        assertEquals(1, pt.simpleParametersAmount());
        assertEquals(2, pt.regExpParametersAmount());
        assertFalse(pt.isSimple());
    }

    @Test //
    public void wrongOpened() {
        assertThrows(IllegalArgumentException.class, () -> new Template("/api/te{idst/som{ddd}ething"));
    }

    @Test //
    public void wrongCharacter() {
        assertThrows(IllegalArgumentException.class, () -> new Template("/api/te{ id,ddd  }ething"));
    }

    @Test //
    public void uri() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new Template("sert/}dddd"));
        assertEquals(URISyntaxException.class, exception.getCause().getClass());
    }

    @Test //
    public void wrongOpenBrace() {
        assertThrows(IllegalArgumentException.class, () -> new Template("/api/te{ id {  }ething"));
    }

    @Test //
    public void wrongOnlyOpenBrace() {
        assertThrows(IllegalArgumentException.class, () -> new Template("/api/te{ id ething"));
    }

    @Test //
    public void wrongOpenBraceAndSpace() {
        assertThrows(IllegalArgumentException.class, () -> new Template("/api/te{   "));
    }

    @Test //
    public void equals() {
        Template t1 = new Template("/api/{var}");
        Template t2 = new Template("/api/{var}");
        assertTrue(t1.equals(t2));
        assertTrue(t1.equals(t1));
    }

    @Test //
    public void notEquals() {
        Template t1 = new Template("/api/{var1}");
        Template t2 = new Template("/api/{var2}");
        assertFalse(t1.equals(t2));
    }

    @Test //
    public void notEqualsRegExp() {
        Template t1 = new Template("/api/{var}");
        Template t2 = new Template("/api/{ var : \\d+ }");
        assertFalse(t1.equals(t2));
    }

    @Test //
    public void notEqualsObject() {
        Template t1 = new Template("/api/{var1}");
        assertFalse(t1.equals(new Object()));
    }

    @Test //
    public void hashCod() {
        Template t1 = new Template("/api/{var1}");
        Template t2 = new Template("/api/{var1}");
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}
