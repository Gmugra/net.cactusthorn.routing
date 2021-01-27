package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.QueryParam;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingInitializationException;

public class QueryParamParameterTest extends InvokeTestAncestor {

    public static class EntryPoint1 {

        public void wrong(@QueryParam("val") int values) {
        }

        public void queue(@QueryParam("val") Queue<Integer> values) {
        }

        public void list(@QueryParam("val") List<Integer> values) {
        }

        public void sortedSet(@QueryParam("val") SortedSet<Integer> values) {
        }

        public void set(@QueryParam("val") Set<Integer> values) {
        }

        @SuppressWarnings("rawtypes") //
        public void collectionNoGeneric(@QueryParam("val") List values) {
        }
    }

    public static class EntryPoint1Provider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new EntryPoint1();
        }
    }

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new EntryPoint1Provider()).addResource(EntryPoint1.class).build();

    @ParameterizedTest @MethodSource("collectionArguments") //
    public void collections(String methodName, String[] requestValues, Integer[] expected) throws Exception {
        Method m = findMethod(EntryPoint1.class, methodName);
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, m.getGenericParameterTypes()[0], CONFIG, DEFAULT_CONTENT_TYPES);

        Mockito.when(request.getParameterValues("val")).thenReturn(requestValues);

        Object result = mp.findValue(request, null, null, null);

        assertArrayEquals(expected, ((Collection<?>) result).toArray());
    }

    private static Stream<Arguments> collectionArguments() {
        // @formatter:off
        return Stream.of(
            Arguments.of("sortedSet", new String[] {"10", "20", "20"}, new Integer[] {10, 20}),
            Arguments.of("list", new String[] {"10", "20", "30"}, new Integer[] {10, 20, 30}));
        // @formatter:on
    }

    @Test
    public void set() throws Exception {
        Method m = findMethod(EntryPoint1.class, "set");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, m.getGenericParameterTypes()[0], CONFIG, DEFAULT_CONTENT_TYPES);

        Mockito.when(request.getParameterValues("val")).thenReturn(new String[] {"10", "20", "20"});

        Set<?> result = (Set<?>)mp.findValue(request, null, null, null);

        assertEquals(2, result.size());
    }

    @ParameterizedTest @ValueSource(strings = { "collectionNoGeneric"}) //
    public void testThrows(String method) {
        Method m = findMethod(EntryPoint1.class, method);
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, null, CONFIG, DEFAULT_CONTENT_TYPES));
    }

    /**
     * According to JSR-339 supported only List<T>, Set<T>, or SortedSet<T>
     */
    @Test //
    public void collectionWrongType() throws Exception {
        Method m = findMethod(EntryPoint1.class, "queue");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, null, CONFIG, DEFAULT_CONTENT_TYPES));
    }

    @Test //
    public void wrong() {
        Method m = findMethod(EntryPoint1.class, "wrong");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, null, CONFIG, DEFAULT_CONTENT_TYPES);

        Mockito.when(request.getParameter("val")).thenReturn("abc");

        assertThrows(NotFoundException.class, () -> mp.findValue(request, null, null, null));
    }
}
