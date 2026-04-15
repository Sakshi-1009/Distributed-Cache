package database;

public interface Database<K, V> {
    V fetch(K key);
    void save(K key, V value);
}
