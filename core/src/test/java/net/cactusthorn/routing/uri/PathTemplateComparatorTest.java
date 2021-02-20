package net.cactusthorn.routing.uri;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class PathTemplateComparatorTest {

    @Test //
    public void testNull() {
        assertEquals(0, PathTemplate.COMPARATOR.compare(null, null));
    }

    @Test //
    public void testNullLess() {
        PathTemplate t = new PathTemplate("/a");
        assertTrue(PathTemplate.COMPARATOR.compare(null, t) > 0);
    }

    @Test //
    public void testNullMore() {
        PathTemplate t = new PathTemplate("/a");
        assertTrue(PathTemplate.COMPARATOR.compare(t, null) < 0);
    }

    @Test //
    public void testLongerWin() {
        PathTemplate t1 = new PathTemplate("/a/b");
        PathTemplate t2 = new PathTemplate("/a");
        assertTrue(PathTemplate.COMPARATOR.compare(t1, t2) < 0);
    }

    @Test //
    public void testLongerWinAgainsParams() {
        PathTemplate t1 = new PathTemplate("/a/b");
        PathTemplate t2 = new PathTemplate("/a{var}/");
        assertTrue(PathTemplate.COMPARATOR.compare(t1, t2) < 0);
    }

    @Test //
    public void testSimpleSame() {
        PathTemplate t1 = new PathTemplate("/a/b");
        PathTemplate t2 = new PathTemplate("  /a/b   ");
        assertTrue(PathTemplate.COMPARATOR.compare(t1, t2) == 0);
    }

    @Test //
    public void testMoreParamsWin() {
        PathTemplate t1 = new PathTemplate("/a{var}/b{var1}");
        PathTemplate t2 = new PathTemplate("/a{var}/b");
        assertTrue(PathTemplate.COMPARATOR.compare(t1, t2) < 0);
    }

    @Test //
    public void testMoreRegExpParamsWin() {
        PathTemplate t1 = new PathTemplate("/a{ var : abc }/b{var1}");
        PathTemplate t2 = new PathTemplate("/a{var}/b{var1}");
        assertTrue(PathTemplate.COMPARATOR.compare(t1, t2) < 0);
    }
}
