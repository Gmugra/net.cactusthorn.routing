package net.cactusthorn.routing.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UnmodifiableMultivaluedMapTest {

    static MultivaluedMap<String, String> MAP = new MultivaluedHashMap<>();
    static List<String> LIST = Arrays.asList("L1", "L2");
    static UnmodifiableMultivaluedMap<String, String> U;

    @BeforeAll
    public static void setUp() {
        MAP.add("A", "b");
        MAP.addFirst("A", "c");
        MAP.add("A", "f");
        MAP.put("B", LIST);
        MAP.add("Z", null);
        U = new UnmodifiableMultivaluedMap<>(MAP);
    }

    @Test
    public void validate() {
        assertEquals("c", U.getFirst("A"));
        assertTrue(U.equalsIgnoreValueOrder(MAP));
        assertEquals(3, U.size());
        assertFalse(U.isEmpty());
        assertTrue(U.containsKey("B"));
        assertTrue(U.containsValue(LIST));
        assertNull(U.get("W"));
        assertEquals(3, U.get("A").size());
        assertEquals(3, U.keySet().size());
        assertEquals(3, U.values().size());
        assertEquals(3, U.entrySet().size());
    }

    @Test
    public void modifySimple() {

        // MultivaluedMap
        assertThrows(UnsupportedOperationException.class, () -> U.putSingle("z", "y"));
        assertThrows(UnsupportedOperationException.class, () -> U.add("z", "y"));
        assertThrows(UnsupportedOperationException.class, () -> U.addAll("z", "y", "w"));
        assertThrows(UnsupportedOperationException.class, () -> U.addAll("z", LIST));
        assertThrows(UnsupportedOperationException.class, () -> U.addFirst("z", "A"));

        // Map
        assertThrows(UnsupportedOperationException.class, () -> U.put("z", LIST));
        assertThrows(UnsupportedOperationException.class, () -> U.remove("A"));
        assertThrows(UnsupportedOperationException.class, () -> U.putAll(null));
        assertThrows(UnsupportedOperationException.class, () -> U.clear());
    }

    @Test
    public void modifySubObjects() {

        List<String> values = U.get("A");
        assertThrows(UnsupportedOperationException.class, () -> values.add("W"));

        Set<String> keys = U.keySet();
        assertThrows(UnsupportedOperationException.class, () -> keys.add("W"));

        Collection<List<String>> allValues = U.values();
        assertThrows(UnsupportedOperationException.class, () -> allValues.add(LIST));
    }

    @Test
    public void modifyEntrySet() {

        Set<Map.Entry<String, List<String>>> entries = U.entrySet();
        Map.Entry<String, List<String>> entry = entries.iterator().next();

        assertNotNull(entry.getKey());
        assertNotNull(entry.getValue());
        assertThrows(UnsupportedOperationException.class, () -> entries.add(null));
        assertThrows(UnsupportedOperationException.class, () -> entry.setValue(LIST));
    }
    
    @Test
    public void object() {
        assertEquals(MAP.toString(), U.toString());
        assertTrue(U.equals(MAP));
        assertEquals(MAP.hashCode(), U.hashCode());
    }
}
