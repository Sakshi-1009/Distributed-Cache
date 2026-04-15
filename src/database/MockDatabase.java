package database;

import java.util.HashMap;
import java.util.Map;

public class MockDatabase<K, V> implements Database<K, V> {
    private final Map<K, V> dbStorage = new HashMap<>();

    @Override
    public V fetch(K key) {
        System.out.println("[Database] Fetching key -> " + key + " from database.");
        return dbStorage.get(key);
    }

    @Override
    public void save(K key, V value) {
        System.out.println("[Database] Saving key -> " + key + " into database.");
        dbStorage.put(key, value);
    }
}
