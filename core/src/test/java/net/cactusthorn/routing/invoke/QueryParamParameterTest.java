package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.RequestData;
import net.cactusthorn.routing.annotation.QueryParam;

public class QueryParamParameterTest extends InvokeTestAncestor {

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

    @Test //
    public void collectionNoGeneric() {
        Method m = findMethod(EntryPoint1.class, "collectionNoGeneric");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @Test //
    public void linkedList() throws Exception {
        Method m = findMethod(EntryPoint1.class,"linkedList");
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
        Method m = findMethod(EntryPoint1.class, "sortedSet");
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
        Method m = findMethod(EntryPoint1.class, "set");
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
        Method m = findMethod(EntryPoint1.class, "list");
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
        Method m = findMethod(EntryPoint1.class, "collection");
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
    public void nullCollection() throws Exception {
        Method m = findMethod(EntryPoint1.class, "collection");
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
        Method m = findMethod(EntryPoint1.class, "array");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getParameterValues("val")).thenReturn(new String[] { "100", "200" });
        RequestData data = new RequestData(null);

        int[] result = (int[]) mp.findValue(request, null, null, data);
        assertArrayEquals(new int[] { 100, 200 }, result);
    }

    @Test //
    public void multiArray() {
        Method m = findMethod(EntryPoint1.class, "multiArray");
        Parameter p = m.getParameters()[0];
        assertThrows(RoutingInitializationException.class, () -> MethodParameter.Factory.create(m, p, HOLDER, "*/*"));
    }

    @Test //
    public void wrong() {
        Method m = findMethod(EntryPoint1.class, "wrong");
        Parameter p = m.getParameters()[0];
        MethodParameter mp = MethodParameter.Factory.create(m, p, HOLDER, "*/*");

        Mockito.when(request.getParameter("val")).thenReturn("abc");
        RequestData data = new RequestData(null);

        assertThrows(NumberFormatException.class, () -> mp.findValue(request, null, null, data));
    }
}
