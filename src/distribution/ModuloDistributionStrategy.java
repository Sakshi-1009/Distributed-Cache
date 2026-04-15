package distribution;

import cache.CacheNode;
import java.util.List;

public class ModuloDistributionStrategy<K, V> implements DistributionStrategy<K, V> {
    @Override
    public CacheNode<K, V> getNode(K key, List<CacheNode<K, V>> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException("Cache node list cannot be empty");
        }
        int hashCode = key.hashCode();
        if (hashCode == Integer.MIN_VALUE) { 
            // Math.abs(Integer.MIN_VALUE) is negative so handle it manually
            hashCode = 0; 
        }
        int targetIndex = Math.abs(hashCode) % nodes.size();
        return nodes.get(targetIndex);
    }
}
