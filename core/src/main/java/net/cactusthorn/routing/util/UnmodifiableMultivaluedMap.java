package net.cactusthorn.routing.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.MultivaluedMap;

public class UnmodifiableMultivaluedMap<K, V> implements MultivaluedMap<K, V> {

    private final MultivaluedMap<K, V> map;

    private final Set<Map.Entry<K, List<V>>> entrySet;

    public UnmodifiableMultivaluedMap(MultivaluedMap<K, V> multivaluedMap) {
        this.map = multivaluedMap;

        Set<Map.Entry<K, List<V>>> result = new HashSet<>();
        for (Map.Entry<K, List<V>> entry : map.entrySet()) {
            result.add(new UnmodifiableEntry(entry));
        }
        entrySet = Collections.unmodifiableSet(result);
    }

    private final class UnmodifiableEntry implements Map.Entry<K, List<V>> {

        private final Map.Entry<K, List<V>> entry;

        private UnmodifiableEntry(Map.Entry<K, List<V>> entry) {
            this.entry = entry;
        }

        @Override public K getKey() {
            return entry.getKey();
        }

        @Override public List<V> getValue() {
            List<V> value = entry.getValue();
            return value == null ? null : Collections.unmodifiableList(value);
        }

        @Override public List<V> setValue(List<V> value) {
            throw new UnsupportedOperationException();
        }
    }

    // MultivaluedMap {
    @Override public void putSingle(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override public void add(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override public V getFirst(K key) {
        return map.getFirst(key);
    }

    @Override @SuppressWarnings(value = "unchecked") public void addAll(K key, V... newValues) {
        throw new UnsupportedOperationException();
    }

    @Override public void addAll(K key, List<V> valueList) {
        throw new UnsupportedOperationException();
    }

    @Override public void addFirst(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override public boolean equalsIgnoreValueOrder(MultivaluedMap<K, V> otherMap) {
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
        return map.containsKey(key);
    }

    @Override public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override public List<V> get(Object key) {
        List<V> value = map.get(key);
        return value == null ? null : Collections.unmodifiableList(value);
    }

    @Override public List<V> put(K key, List<V> value) {
        throw new UnsupportedOperationException();
    }

    @Override public List<V> remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override public void putAll(Map<? extends K, ? extends List<V>> m) {
        throw new UnsupportedOperationException();
    }

    @Override public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override public Set<K> keySet() {
        return Collections.unmodifiableSet(map.keySet());
    }

    @Override public Collection<List<V>> values() {
        return Collections.unmodifiableCollection(map.values());
    }

    @Override public Set<Entry<K, List<V>>> entrySet() {
        return entrySet;
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
