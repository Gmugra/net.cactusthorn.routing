package net.cactusthorn.routing.template;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.Template;

public class TemplateComparatorTest {

    @Test //
    public void testNull() {
        assertEquals(0, Template.COMPARATOR.compare(null, null));
    }

    @Test //
    public void testNullLess() {
        Template t = new Template("/a");
        assertTrue(Template.COMPARATOR.compare(null, t) > 0);
    }

    @Test //
    public void testNullMore() {
        Template t = new Template("/a");
        assertTrue(Template.COMPARATOR.compare(t, null) < 0);
    }

    @Test //
    public void testLongerWin() {
        Template t1 = new Template("/a/b");
        Template t2 = new Template("/a");
        assertTrue(Template.COMPARATOR.compare(t1, t2) < 0);
    }

    @Test //
    public void testLongerWinAgainsParams() {
        Template t1 = new Template("/a/b");
        Template t2 = new Template("/a{var}/");
        assertTrue(Template.COMPARATOR.compare(t1, t2) < 0);
    }

    @Test //
    public void testSimpleSame() {
        Template t1 = new Template("/a/b");
        Template t2 = new Template("  /a/b   ");
        assertTrue(Template.COMPARATOR.compare(t1, t2) == 0);
    }

    @Test //
    public void testMoreParamsWin() {
        Template t1 = new Template("/a{var}/b{var1}");
        Template t2 = new Template("/a{var}/b");
        assertTrue(Template.COMPARATOR.compare(t1, t2) < 0);
    }

    @Test //
    public void testMoreRegExpParamsWin() {
        Template t1 = new Template("/a{ var : abc }/b{var1}");
        Template t2 = new Template("/a{var}/b{var1}");
        assertTrue(Template.COMPARATOR.compare(t1, t2) < 0);
    }
}
