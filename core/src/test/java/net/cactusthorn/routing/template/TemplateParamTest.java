package net.cactusthorn.routing.template;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.regex.PatternSyntaxException;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.Template;

public class TemplateParamTest {

    private static final Set<String> PARAM_NAMES = new HashSet<>(Arrays.asList("id", "ddd", "some"));

    @Test //
    public void single() {
        Template pt = new Template("/api/{id}/bbb");
        List<String> parameters = pt.parameters();
        assertEquals(1, parameters.size());
        assertFalse(pt.isSimple());
    }

    @Test //
    public void simple() {
        Template pt = new Template("/api/te{id}st/som{ddd}ething{some}");
        List<String> parameters = pt.parameters();
        assertEquals(3, parameters.size());
        assertTrue(parameters.containsAll(PARAM_NAMES));
        assertFalse(pt.isSimple());
    }

    @Test //
    public void withSpaces() {
        Template pt = new Template("/api/te{id  }st/som{   ddd}ething{  some\t}");
        List<String> parameters = pt.parameters();
        assertEquals(3, parameters.size());
        assertTrue(parameters.containsAll(PARAM_NAMES));
        assertFalse(pt.isSimple());
    }

    @Test //
    public void withRegExp() {
        Template pt = new Template("/api/te{id : ab}st/som{   ddd :ffff}ething{  some:   rtz\t}");
        List<String> parameters = pt.parameters();
        assertEquals(3, parameters.size());
        assertTrue(parameters.containsAll(PARAM_NAMES));
        assertFalse(pt.isSimple());
    }

    @Test //
    public void mixed() {
        Template pt = new Template("/api/te{id}st/som{   ddd:ffff}ething{some: rtz\t}");
        List<String> parameters = pt.parameters();
        assertEquals(3, parameters.size());
        assertTrue(parameters.containsAll(PARAM_NAMES));
        assertFalse(pt.isSimple());
    }

    @Test //
    public void regExp() {
        Template pt = new Template("/api/te{id : ab}st/som{   ddd :ffff}ething{  some:   rtz\t}");
        String pattern = pt.pattern().pattern();
        assertEquals("/api/te(ab)st/som(ffff)ething(rtz)", pattern);
        assertFalse(pt.isSimple());
    }

    @Test //
    public void regExpWithSimple() {
        Template pt = new Template("/api/te{id : ab}st/som{   ddd }ething{  some:   rtz\t}");
        String pattern = pt.pattern().pattern();
        assertEquals("/api/te(ab)st/som([^/]+)ething(rtz)", pattern);
        assertFalse(pt.isSimple());
    }

    @Test //
    public void regExpInnerSpace() {
        Template pt = new Template("/api/test/som{ddd : aa bb }ething");
        String pattern = pt.pattern().pattern();
        assertEquals("/api/test/som(aa bb)ething", pattern);
        assertFalse(pt.isSimple());
    }

    @Test //
    public void patternException() {
        assertThrows(PatternSyntaxException.class, () -> new Template("/api/test/som{ddd : aa[^bb }ething"));
    }

    @Test //
    public void patternWithInnerBraces() {
        Template pt = new Template("/api/test/som{ ddd : \\d{2} }");
        String pattern = pt.pattern().pattern();
        assertEquals("/api/test/som(\\d{2})", pattern);
        assertFalse(pt.isSimple());
    }

    @Test //
    public void patternWithNotClosedBraces() {
        assertThrows(IllegalArgumentException.class, () -> new Template("/api/test/som{ddd : aa{ething"));
    }

    @Test //
    public void patternMultipleNotSame() {
        assertThrows(IllegalArgumentException.class, () -> new Template("/api/test{ddd}/som{ddd : aaaa}"));
    }

    @Test //
    public void patternMultiple() {
        Template pt = new Template("/api/test{ ddd:aaaa }/som{ddd : aaaa}");
        assertEquals(2, pt.regExpParametersAmount());
    }
}
