package eviction;

public interface EvictionPolicy<K> {
    void keyAccessed(K key);
    K evictKey();
    void removeKey(K key);
}
