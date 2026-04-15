package cache;

import database.Database;
import distribution.DistributionStrategy;

import java.util.List;

public class DistributedCache<K, V> {
    private final List<CacheNode<K, V>> nodes;
    private final DistributionStrategy<K, V> targetStrategy;
    private final Database<K, V> db;

    public DistributedCache(List<CacheNode<K, V>> nodes, DistributionStrategy<K, V> strategy, Database<K, V> db) {
        this.nodes = nodes;
        this.targetStrategy = strategy;
        this.db = db;
    }

    public V get(K key) {
        CacheNode<K, V> targetNode = targetStrategy.getNode(key, nodes);
        System.out.println("[DistributedCache] Routing GET for key -> " + key + " to Node -> " + targetNode.getNodeId());
        
        V value = targetNode.get(key);
        if (value == null) {
            System.out.println("[DistributedCache] Cache MISS for key -> " + key + ". Falling back to Database.");
            value = db.fetch(key);
            if (value != null) {
                targetNode.put(key, value);
            }
        } else {
            System.out.println("[DistributedCache] Cache HIT for key -> " + key);
        }
        return value;
    }

    public void put(K key, V value) {
        CacheNode<K, V> targetNode = targetStrategy.getNode(key, nodes);
        System.out.println("[DistributedCache] Routing PUT for key -> " + key + " to Node -> " + targetNode.getNodeId());
        targetNode.put(key, value);
        // Assuming database is also updated asynchronously or synchronously:
        db.save(key, value);
    }
}
