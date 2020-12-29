package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.annotation.QueryParam;
import net.cactusthorn.routing.convert.ConverterException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class QueryParamParameterTest {

    static final ConvertersHolder HOLDER = new ConvertersHolder();

    public static class EntryPoint1 {

        public void array(@QueryParam("val") int[] values) {
        }

        public void multiArray(@QueryParam("val") int[][] values) {
        }

        public void wrong(@QueryParam("val") int values) {
        }

        public void collection(@QueryParam("val") Collection<Integer> values) {
        }

        public void list(@QueryParam("val") List<Integer> values) {
        }

        public void set(@QueryParam("val") Set<Integer> values) {
        }

        public void sortedSet(@QueryParam("val") SortedSet<Integer> values) {
        }

        public void linkedList(@QueryParam("val") LinkedList<Integer> values) {
        }

        @SuppressWarnings("rawtypes") //
        public void collectionNoGeneric(@QueryParam("val") List values) {
        }
    }

    HttpServletRequest request;

    @BeforeEach //
    void setUp() {
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test //
    public void collectionNoGeneric() {
        Method m = findMethod("collectionNoGeneric");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @Test //
    public void linkedList() throws Exception {
        Method m = findMethod("linkedList");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        String[] values = new String[] { "10", "20", "30" };
        Mockito.when(request.getParameterValues("val")).thenReturn(values);

        @SuppressWarnings("unchecked") //
        LinkedList<Integer> collection = (LinkedList<Integer>) mp.findValue(request, null, null, null);

        Integer[] expected = new Integer[] { 10, 20, 30 };
        assertArrayEquals(expected, collection.toArray());
    }

    @Test //
    public void sortedSet() throws Exception {
        Method m = findMethod("sortedSet");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        String[] values = new String[] { "10", "20", "20" };
        Mockito.when(request.getParameterValues("val")).thenReturn(values);

        @SuppressWarnings("unchecked") //
        SortedSet<Integer> collection = (SortedSet<Integer>) mp.findValue(request, null, null, null);

        Integer[] expected = new Integer[] { 10, 20 };
        assertArrayEquals(expected, collection.toArray());
    }

    @Test //
    public void set() throws Exception {
        Method m = findMethod("set");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        String[] values = new String[] { "10", "20", "20" };
        Mockito.when(request.getParameterValues("val")).thenReturn(values);

        @SuppressWarnings("unchecked") //
        Set<Integer> collection = (Set<Integer>) mp.findValue(request, null, null, null);

        assertEquals(2, collection.size());
    }

    @Test //
    public void list() throws Exception {
        Method m = findMethod("list");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        String[] values = new String[] { "10", "20", "30" };
        Mockito.when(request.getParameterValues("val")).thenReturn(values);

        @SuppressWarnings("unchecked") //
        List<Integer> collection = (List<Integer>) mp.findValue(request, null, null, null);

        Integer[] expected = new Integer[] { 10, 20, 30 };
        assertArrayEquals(expected, collection.toArray());
    }

    @Test //
    public void collection() throws Exception {
        Method m = findMethod("collection");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        String[] values = new String[] { "10", "20", "30" };
        Mockito.when(request.getParameterValues("val")).thenReturn(values);

        @SuppressWarnings("unchecked") //
        Collection<Integer> collection = (Collection<Integer>) mp.findValue(request, null, null, null);

        Integer[] expected = new Integer[] { 10, 20, 30 };
        assertArrayEquals(expected, collection.toArray());
    }

    @Test //
    public void nullCcollection() throws Exception {
        Method m = findMethod("collection");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        String[] values = null;
        Mockito.when(request.getParameterValues("val")).thenReturn(values);

        @SuppressWarnings("unchecked") //
        Collection<Integer> collection = (Collection<Integer>) mp.findValue(request, null, null, null);

        assertNull(collection);
    }

    @Test //
    public void array() throws Exception {
        Method m = findMethod("array");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getParameterValues("val")).thenReturn(new String[] { "100", "200" });
        RequestData data = new RequestData(null);

        int[] result = (int[]) mp.findValue(request, null, null, data);
        assertArrayEquals(new int[] { 100, 200 }, result);
    }

    @Test //
    public void multiArray() {
        Method m = findMethod("multiArray");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @Test //
    public void wrong() {
        Method m = findMethod("wrong");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getParameter("val")).thenReturn("abc");
        RequestData data = new RequestData(null);

        assertThrows(NumberFormatException.class, () -> mp.findValue(request, null, null, data));
    }

    private Method findMethod(String methodName) {
        for (Method method : EntryPoint1.class.getMethods()) {
            if (methodName.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }
}
