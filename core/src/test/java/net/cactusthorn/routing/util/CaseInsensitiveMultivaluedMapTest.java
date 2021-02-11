package net.cactusthorn.routing.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CaseInsensitiveMultivaluedMapTest {

    static final List<String> LIST = Arrays.asList("L1", "L2");
    static final List<String> LIST_B = Arrays.asList("B");

    Map<String, List<String>> simpleMap;

    MultivaluedMap<String, String> map;

    @BeforeEach //
    public void setUp() {
        map = new CaseInsensitiveMultivaluedMap<>();
        map.add("AA", "b");
        map.addFirst("AA", "c");
        map.add("AA", "f");
        map.addAll("BBB", LIST_B);

        simpleMap = new HashMap<>();
        simpleMap.put("ZZ", Arrays.asList("Z1"));
        simpleMap.put("zz", Arrays.asList("Z2"));
    }

    @Test //
    public void multivaluedMap() {
        assertEquals("c", map.getFirst("aA"));
        map.putSingle("aa", "z");
        assertEquals("z", map.getFirst("aA"));
        assertEquals(1, map.get("Aa").size());
        map.add("AA", "f");
        assertEquals(2, map.get("Aa").size());
        map.addAll("aa", "f1", "f2");
        assertEquals(4, map.get("Aa").size());
        map.addAll("aA", LIST);
        assertEquals(6, map.get("Aa").size());
        assertEquals(2, map.size());
        assertTrue(map.equalsIgnoreValueOrder(map));
    }

    @Test @SuppressWarnings("unlikely-arg-type") //
    public void map() {
        assertFalse(map.isEmpty());
        assertFalse(map.containsKey(10));
        assertTrue(map.containsValue(LIST_B));
        assertNull(map.get(10));
        assertNotNull(map.remove("bBb"));
        assertNull(map.remove(10));
        map.put("bbB", LIST_B);
        assertTrue(map.containsKey("BBB"));
        map.putAll(simpleMap);
        assertEquals(1, map.get("zZ").size());
        assertNotNull(map.keySet());
        assertNotNull(map.values());
        assertNotNull(map.entrySet());
        map.clear();
        assertTrue(map.isEmpty());
    }

    @Test
    public void object() {
        assertEquals("{aa=[c, b, f], bbb=[B]}", map.toString());
        assertEquals(map, map);
        assertTrue(map.hashCode() > 0);
    }
    
    @Test //
    public void constructor() {
        assertNotNull(new CaseInsensitiveMultivaluedMap<String>(10));
        assertNotNull(new CaseInsensitiveMultivaluedMap<String>(10, 0.75f));
    }

}
