package net.cactusthorn.routing.pathtemplate;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.PathTemplate;

public class PathTemplateSimpleTest {

    @Test //
    public void simple() {
        PathTemplate pt = new PathTemplate("/api/test/something");
        assertEquals(19, pt.literalCharsAmount());
        assertTrue(pt.isSimple());
    }

    @Test //
    public void nullTemplate() {
        assertThrows(IllegalArgumentException.class, () -> new PathTemplate(null));
    }

    @Test //
    public void emptyTemplate() {
        assertThrows(IllegalArgumentException.class, () -> new PathTemplate("\t"));
    }

    @Test //
    public void withSpaces() {
        PathTemplate pt = new PathTemplate("   /api/test/something\t");
        assertEquals(19, pt.literalCharsAmount());
        assertTrue(pt.isSimple());
    }

    @Test //
    public void withSimpleParams() {
        PathTemplate pt = new PathTemplate("/api/te{id}st/som{ddd}ething{ so_me }");
        assertEquals(19, pt.literalCharsAmount());
        assertFalse(pt.isSimple());
    }

    @Test //
    public void withParams() {
        PathTemplate pt = new PathTemplate("/api/te{id : xxxx}st/som{ddd}ething{some: xxx}");
        assertEquals(19, pt.literalCharsAmount());
        assertEquals(1, pt.simpleParametersAmount());
        assertEquals(2, pt.regExpParametersAmount());
        assertFalse(pt.isSimple());
    }

    @Test //
    public void wrongOpened() {
        assertThrows(IllegalArgumentException.class, () -> new PathTemplate("/api/te{idst/som{ddd}ething"));
    }

    @Test //
    public void wrongCharacter() {
        assertThrows(IllegalArgumentException.class, () -> new PathTemplate("/api/te{ id,ddd  }ething"));
    }

    @Test //
    public void uri() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new PathTemplate("sert/}dddd"));
        assertEquals(URISyntaxException.class, exception.getCause().getClass());
    }

    @Test //
    public void wrongOpenBrace() {
        assertThrows(IllegalArgumentException.class, () -> new PathTemplate("/api/te{ id {  }ething"));
    }

    @Test //
    public void wrongOnlyOpenBrace() {
        assertThrows(IllegalArgumentException.class, () -> new PathTemplate("/api/te{ id ething"));
    }

    @Test //
    public void wrongOpenBraceAndSpace() {
        assertThrows(IllegalArgumentException.class, () -> new PathTemplate("/api/te{   "));
    }

    @Test //
    public void equals() {
        PathTemplate t1 = new PathTemplate("/api/{var}");
        PathTemplate t2 = new PathTemplate("/api/{var}");
        assertTrue(t1.equals(t2));
        assertTrue(t1.equals(t1));
    }

    @Test //
    public void notEquals() {
        PathTemplate t1 = new PathTemplate("/api/{var1}");
        PathTemplate t2 = new PathTemplate("/api/{var2}");
        assertFalse(t1.equals(t2));
    }

    @Test //
    public void notEqualsRegExp() {
        PathTemplate t1 = new PathTemplate("/api/{var}");
        PathTemplate t2 = new PathTemplate("/api/{ var : \\d+ }");
        assertFalse(t1.equals(t2));
    }

    @Test //
    public void notEqualsObject() {
        PathTemplate t1 = new PathTemplate("/api/{var1}");
        assertFalse(t1.equals(new Object()));
    }

    @Test //
    public void hashCod() {
        PathTemplate t1 = new PathTemplate("/api/{var1}");
        PathTemplate t2 = new PathTemplate("/api/{var1}");
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}
