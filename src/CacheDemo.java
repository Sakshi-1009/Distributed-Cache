import cache.CacheNode;
import cache.DistributedCache;
import database.Database;
import database.MockDatabase;
import distribution.DistributionStrategy;
import distribution.ModuloDistributionStrategy;
import eviction.EvictionPolicy;
import eviction.LRUEvictionPolicy;
import storage.InMemoryStorage;
import storage.Storage;

import java.util.ArrayList;
import java.util.List;

public class CacheDemo {
    public static void main(String[] args) {
        // 1. Initialize Mock Database
        Database<Integer, String> db = new MockDatabase<>();
        // Seed DB
        db.save(1, "Data1");
        db.save(2, "Data2");
        db.save(3, "Data3");
        db.save(4, "Data4");
        db.save(5, "Data5");

        System.out.println("---------- Initializing Distributed Cache ----------");
        
        // 2. Initialize Cache Nodes (Capacity = 2 per node)
        List<CacheNode<Integer, String>> nodes = new ArrayList<>();
        int capacityPerNode = 2;
        
        for (int i = 0; i < 3; i++) {
            Storage<Integer, String> storage = new InMemoryStorage<>();
            EvictionPolicy<Integer> evictionPolicy = new LRUEvictionPolicy<>();
            nodes.add(new CacheNode<>("Node-" + i, capacityPerNode, storage, evictionPolicy));
        }

        // 3. Set Distribution Strategy
        DistributionStrategy<Integer, String> moduloStrategy = new ModuloDistributionStrategy<>();

        // 4. Create Distributed Cache
        DistributedCache<Integer, String> distributedCache = new DistributedCache<>(nodes, moduloStrategy, db);

        System.out.println("\n---------- Cache Operations ----------");

        // Fetching key 1 (Cache Miss initially, gets from DB)
        System.out.println("\n--- Get Key 1 ---");
        System.out.println("Result: " + distributedCache.get(1));

        // Fetching key 1 again (Cache Hit)
        System.out.println("\n--- Get Key 1 ---");
        System.out.println("Result: " + distributedCache.get(1));

        // Fetching keys to trigger eviction
        System.out.println("\n--- Fill Cache Node With Eviction Trigger ---");
        // We will do several puts/gets to demonstrate eviction
        // Node mapping for keys (using Modulo 3 strategy):
        // 1 % 3 = 1 -> Node-1
        // 2 % 3 = 2 -> Node-2
        // 4 % 3 = 1 -> Node-1
        // 7 % 3 = 1 -> Node-1 (will evict 1 from Node-1 since capacity is 2)
        
        System.out.println("\n--- Get Key 4 (Maps to Node-1) ---");
        distributedCache.get(4); // Node-1 now has keys 1, 4

        System.out.println("\n--- Get Key 7 (Maps to Node-1, triggers eviction) ---");
        // Let's seed DB with 7 first
        db.save(7, "Data7");
        distributedCache.get(7); // Node-1 is full, evicts oldest (Key 1), adds Key 7

        System.out.println("\n--- Get Key 1 Again (Evicted, leads to Cache Miss) ---");
        distributedCache.get(1); // Fetches from DB again
    }
}
