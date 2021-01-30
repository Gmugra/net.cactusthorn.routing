package net.cactusthorn.routing.invoke;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.QueryParam;

import org.junit.jupiter.api.Test;

import net.cactusthorn.routing.ComponentProvider;
import net.cactusthorn.routing.RoutingConfig;
import net.cactusthorn.routing.RoutingInitializationException;
import net.cactusthorn.routing.convert.ConvertersHolder;

public class ParameterInfoTest extends InvokeTestAncestor {

    public static class TestIt {

        public void set(@QueryParam("") Set<Integer> input) {
        }

        public void withoutGeneric(@SuppressWarnings("rawtypes") @QueryParam("") Set input) {
        }

        public void list(@FormParam("") List<Double> input) {
        }

        public void sortedSet(@FormParam("") SortedSet<UUID> input) {
        }

        public void collection(@QueryParam("") Collection<String> input) {
        }
    }

    public static class TestItProvider implements ComponentProvider {

        @Override //
        public Object provide(Class<?> clazz, HttpServletRequest request) {
            return new TestIt();
        }
    }

    private static final RoutingConfig CONFIG = RoutingConfig.builder(new TestItProvider()).build();

    @Test //
    public void set() throws Exception {
        ParameterInfo info = parameterInfo(TestIt.class, "set", CONFIG);

        assertTrue(info.collection());

        Set<Object> set = (Set<Object>)info.convert(new String[0]);
        assertTrue(set.isEmpty());

        set = (Set<Object>)info.convert((String[]) null);
        assertTrue(set.isEmpty());

        List<Integer> expected = Arrays.asList(10, 20, 20);
        Set<Object> set2 = (Set<Object>)info.convert(new String[] { "10", "20" });
        assertEquals(2, set2.size());
        assertTrue(set2.containsAll(expected));
        assertThrows(UnsupportedOperationException.class, () -> set2.add((Object)40));
    }

    /**
     * According to JSR-339 collections without generic are not supported
     */
    @Test //
    public void withoutGeneric() {
        Method method = findMethod(TestIt.class, "withoutGeneric");
        Parameter parameter = method.getParameters()[0];
        Type genericType = method.getGenericParameterTypes()[0];

        ConvertersHolder holder = new ConvertersHolder();
        assertThrows(RoutingInitializationException.class, () -> new ParameterInfo(method, parameter, genericType, 0, holder));
    }

    @Test //
    public void list() throws Exception {
        ParameterInfo info = parameterInfo(TestIt.class, "list", CONFIG);

        List<?> list2 = (List<?>)info.convert(new String[0]);
        assertTrue(list2.isEmpty());

        list2 = (List<?>)info.convert((String[]) null);
        assertTrue(list2.isEmpty());

        Double[] expected = new Double[] { 1.1d, 2.2d };
        List<Object> list = (List<Object>)info.convert(new String[] { "1.1", "2.2" });
        assertArrayEquals(expected, list.toArray());
        assertThrows(UnsupportedOperationException.class, () -> list.add((Object)3.3));
    }

    @Test //
    public void sortedSet() throws Exception {
        ParameterInfo info = parameterInfo(TestIt.class, "sortedSet", CONFIG);

        SortedSet<?> sortedSet2 = (SortedSet<?>)info.convert(new String[0]);
        assertTrue(sortedSet2.isEmpty());

        sortedSet2 = (SortedSet<?>)info.convert((String[]) null);
        assertTrue(sortedSet2.isEmpty());

        String expected = "46400000-8cc0-11bd-b43e-10d46e4ef14d";
        SortedSet<?> sortedSet = (SortedSet<?>)info.convert(new String[] { "46400000-8cc0-11bd-b43e-10d46e4ef14d" });
        assertEquals(expected, sortedSet.first().toString());
        assertThrows(UnsupportedOperationException.class, () -> sortedSet.add(null));
    }

    /**
     * According to JSR-339 supported only List<T>, Set<T>, or SortedSet<T>; nothing
     * else
     */
    @Test //
    public void collection() {
        Method method = findMethod(TestIt.class, "collection");
        Parameter parameter = method.getParameters()[0];
        Type genericType = method.getGenericParameterTypes()[0];

        ConvertersHolder holder = new ConvertersHolder();
        assertThrows(RoutingInitializationException.class, () -> new ParameterInfo(method, parameter, genericType, 0, holder));
    }
}
