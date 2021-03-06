package net.cactusthorn.routing.uri;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class PathTemplateParamTest {

    private static final Set<String> PARAM_NAMES = new HashSet<>(Arrays.asList("id", "ddd", "some"));

    @Test //
    public void single() {
        PathTemplate pt = new PathTemplate("/api/{id}/bbb");
        List<Template.TemplateVariable> parameters = pt.variables();
        assertEquals(1, parameters.size());
        assertFalse(pt.isSimple());
    }

    @Test //
    public void simple() {
        PathTemplate pt = new PathTemplate("/api/te{id}st/som{ddd}ething{some}");
        assertEquals(3, pt.variables().size());
        Set<String> names = pt.variables().stream().map(v -> v.name()).collect(Collectors.toSet());
        assertTrue(names.containsAll(PARAM_NAMES));
        assertFalse(pt.isSimple());
    }

    @Test //
    public void withSpaces() {
        PathTemplate pt = new PathTemplate("/api/te{id  }st/som{   ddd}ething{  some\t}");
        assertEquals(3, pt.variables().size());
        Set<String> names = pt.variables().stream().map(v -> v.name()).collect(Collectors.toSet());
        assertTrue(names.containsAll(PARAM_NAMES));
        assertFalse(pt.isSimple());
    }

    @Test //
    public void withRegExp() {
        PathTemplate pt = new PathTemplate("/api/te{id : ab}st/som{   ddd :ffff}ething{  some:   rtz\t}");
        assertEquals(3, pt.variables().size());
        Set<String> names = pt.variables().stream().map(v -> v.name()).collect(Collectors.toSet());
        assertTrue(names.containsAll(PARAM_NAMES));
        assertFalse(pt.isSimple());
    }

    @Test //
    public void mixed() {
        PathTemplate pt = new PathTemplate("/api/te{id}st/som{   ddd:ffff}ething{some: rtz\t}");
        assertEquals(3, pt.variables().size());
        Set<String> names = pt.variables().stream().map(v -> v.name()).collect(Collectors.toSet());
        assertTrue(names.containsAll(PARAM_NAMES));
        assertFalse(pt.isSimple());
    }

    @Test //
    public void regExp() {
        PathTemplate pt = new PathTemplate("/api/te{id : ab}st/som{   ddd :ffff}ething{  some:   rtz\t}");
        String pattern = pt.pattern().pattern();
        assertEquals("/api/te(ab)st/som(ffff)ething(rtz)", pattern);
        assertFalse(pt.isSimple());
    }

    @Test //
    public void regExpWithSimple() {
        PathTemplate pt = new PathTemplate("/api/te{id : ab}st/som{   ddd }ething{  some:   rtz\t}");
        String pattern = pt.pattern().pattern();
        assertEquals("/api/te(ab)st/som([^/]+)ething(rtz)", pattern);
        assertFalse(pt.isSimple());
    }

    @Test //
    public void regExpInnerSpace() {
        PathTemplate pt = new PathTemplate("/api/test/som{ddd : aa bb }ething");
        String pattern = pt.pattern().pattern();
        assertEquals("/api/test/som(aa bb)ething", pattern);
        assertFalse(pt.isSimple());
    }

    @Test //
    public void patternException() {
        assertThrows(PatternSyntaxException.class, () -> new PathTemplate("/api/test/som{ddd : aa[^bb }ething"));
    }

    @Test //
    public void patternWithInnerBraces() {
        PathTemplate pt = new PathTemplate("/api/test/som{ ddd : \\d{2} }");
        String pattern = pt.pattern().pattern();
        assertEquals("/api/test/som(\\d{2})", pattern);
        assertFalse(pt.isSimple());
    }

    @Test //
    public void patternWithNotClosedBraces() {
        assertThrows(IllegalArgumentException.class, () -> new PathTemplate("/api/test/som{ddd : aa{ething"));
    }

    @Test //
    public void patternMultipleNotSame() {
        assertThrows(IllegalArgumentException.class, () -> new PathTemplate("/api/test{ddd}/som{ddd : aaaa}"));
    }

    @Test //
    public void patternMultiple() {
        PathTemplate pt = new PathTemplate("/api/test{ ddd:aaaa }/som{ddd : aaaa}");
        assertEquals(2, pt.regExpParamsAmount());
    }
}
