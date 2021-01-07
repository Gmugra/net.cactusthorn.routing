package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.annotation.QueryParam;

public class QueryParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void array(@QueryParam("val") Integer[] values) {
        }

        public void arrayByName(@QueryParam Integer[] val) {
        }

        public void multiArray(@QueryParam("val") int[][] values) {
        }

        public void wrong(@QueryParam("val") int values) {
        }

        public void collection(@QueryParam("val") Collection<Integer> values) {
        }

        public void list(@QueryParam("val") List<Integer> values) {
        }

        public void sortedSet(@QueryParam("val") SortedSet<Integer> values) {
        }

        public void linkedList(@QueryParam("val") LinkedList<Integer> values) {
        }

        @SuppressWarnings("rawtypes") //
        public void collectionNoGeneric(@QueryParam("val") List values) {
        }
    }

    @ParameterizedTest @MethodSource("collectionArguments") //
    public void collections(String methodName, String[] requestValues, Integer[] expected) throws Exception {
        Method m = findMethod(EntryPoint1.class, methodName);
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, DEFAULT_CONTENT_TYPES);

        Mockito.when(request.getParameterValues("val")).thenReturn(requestValues);

        Object result = mp.findValue(request, null, null, null);

        if (result.getClass().isArray()) {
            assertArrayEquals(expected, (Integer[]) result);
        } else {
            assertArrayEquals(expected, ((Collection<?>) result).toArray());
        }
    }

    private static Stream<Arguments> collectionArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("linkedList", new String[] {"10", "20", "30"}, new Integer[] {10, 20, 30}),
            Arguments.of("sortedSet", new String[] {"10", "20", "20"}, new Integer[] {10, 20}),
            Arguments.of("list", new String[] {"10", "20", "30"}, new Integer[] {10, 20, 30}),
            Arguments.of("collection", new String[] {"10", "20", "30"}, new Integer[] {10, 20, 30}),
            Arguments.of("array", new String[] {"100", "200"}, new Integer[] {100, 200}),
            Arguments.of("arrayByName", new String[] {"100", "200"}, new Integer[] {100, 200}));
        // @formatter:on
    }

    @ParameterizedTest @ValueSource(strings = { "collectionNoGeneric", "multiArray" }) //
    public void testThrows(String method) {
        Method m = findMethod(EntryPoint1.class, method);
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, DEFAULT_CONTENT_TYPES));
    }

    @Test //
    public void nullCollection() throws Exception {
        Method m = findMethod(EntryPoint1.class, "collection");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, DEFAULT_CONTENT_TYPES);

        Mockito.when(request.getParameterValues("val")).thenReturn(null);

        @SuppressWarnings("unchecked") //
        Collection<Integer> collection = (Collection<Integer>) mp.findValue(request, null, null, null);

        assertNull(collection);
    }

    @Test //
    public void wrong() {
        Method m = findMethod(EntryPoint1.class, "wrong");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, DEFAULT_CONTENT_TYPES);

        Mockito.when(request.getParameter("val")).thenReturn("abc");

        assertThrows(NumberFormatException.class, () -> mp.findValue(request, null, null, null));
    }
}
