package net.cactusthorn.routing.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

public class CaseInsensitiveMultivaluedMap<V> implements MultivaluedMap<String, V> {

    private final MultivaluedMap<String, V> map;

    public CaseInsensitiveMultivaluedMap() {
        map = new MultivaluedHashMap<>();
    }

    public CaseInsensitiveMultivaluedMap(int initialCapacity) {
        map = new MultivaluedHashMap<>(initialCapacity);
    }

    public CaseInsensitiveMultivaluedMap(int initialCapacity, float loadFactor) {
        map = new MultivaluedHashMap<>(initialCapacity, loadFactor);
    }

    // MultivaluedMap {
    @Override public void putSingle(String key, V value) {
        map.putSingle(key.toLowerCase(), value);
    }

    @Override public void add(String key, V value) {
        map.add(key.toLowerCase(), value);
    }

    @Override public V getFirst(String key) {
        return map.getFirst(key.toLowerCase());
    }

    @Override @SuppressWarnings("unchecked") public void addAll(String key, V... newValues) {
        map.addAll(key.toLowerCase(), newValues);
    }

    @Override public void addAll(String key, List<V> valueList) {
        map.addAll(key.toLowerCase(), valueList);
    }

    @Override public void addFirst(String key, V value) {
        map.addFirst(key.toLowerCase(), value);
    }

    @Override public boolean equalsIgnoreValueOrder(MultivaluedMap<String, V> otherMap) {
        return map.equalsIgnoreValueOrder(otherMap);
    }
    // MultivaluedMap }

    // Map {
    @Override public int size() {
        return map.size();
    }

    @Override public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override public boolean containsKey(Object key) {
        if (!String.class.isAssignableFrom(key.getClass())) {
            return false;
        }
        return map.containsKey(((String) key).toLowerCase());
    }

    @Override public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override public List<V> get(Object key) {
        if (!String.class.isAssignableFrom(key.getClass())) {
            return null;
        }
        return map.get(((String) key).toLowerCase());
    }

    @Override public List<V> put(String key, List<V> value) {
        return map.put(key.toLowerCase(), value);
    }

    @Override public List<V> remove(Object key) {
        if (!String.class.isAssignableFrom(key.getClass())) {
            return null;
        }
        return map.remove(((String) key).toLowerCase());
    }

    @Override public void putAll(Map<? extends String, ? extends List<V>> m) {
        for (Map.Entry<? extends String, ? extends List<V>> entry : m.entrySet()) {
            map.put(entry.getKey().toLowerCase(), entry.getValue());
        }
    }

    @Override public void clear() {
        map.clear();

    }

    @Override public Set<String> keySet() {
        Set<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        result.addAll(map.keySet());
        return result;
    }

    @Override public Collection<List<V>> values() {
        return map.values();
    }

    @Override public Set<Entry<String, List<V>>> entrySet() {
        return map.entrySet();
    }
    // Map }

    @Override public String toString() {
        return map.toString();
    }

    @Override public int hashCode() {
        return map.hashCode();
    }

    @Override public boolean equals(Object obj) {
        return map.equals(obj);
    }
}
