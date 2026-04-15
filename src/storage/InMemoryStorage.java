package storage;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStorage<K, V> implements Storage<K, V> {
    private final Map<K, V> map = new HashMap<>();

    @Override
    public void add(K key, V value) {
        map.put(key, value);
    }

    @Override
    public void remove(K key) {
        map.remove(key);
    }

    @Override
    public V get(K key) {
        return map.get(key);
    }

    @Override
    public boolean contains(K key) {
        return map.containsKey(key);
    }
}
