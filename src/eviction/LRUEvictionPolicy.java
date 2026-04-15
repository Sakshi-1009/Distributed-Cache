package eviction;

import java.util.HashMap;
import java.util.Map;

public class LRUEvictionPolicy<K> implements EvictionPolicy<K> {

    // Doubly linked list node to maintain the recent access order
    private static class Node<K> {
        K key;
        Node<K> prev;
        Node<K> next;

        Node(K key) {
            this.key = key;
        }
    }

    private final Map<K, Node<K>> mapper;
    private final Node<K> head; // dummy head
    private final Node<K> tail; // dummy tail

    public LRUEvictionPolicy() {
        this.mapper = new HashMap<>();
        this.head = new Node<>(null);
        this.tail = new Node<>(null);
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void keyAccessed(K key) {
        if (mapper.containsKey(key)) {
            Node<K> node = mapper.get(key);
            removeNode(node);
            addNodeToTail(node);
        } else {
            Node<K> newNode = new Node<>(key);
            mapper.put(key, newNode);
            addNodeToTail(newNode);
        }
    }

    @Override
    public K evictKey() {
        if (head.next == tail) {
            return null; // empty
        }
        Node<K> leastRecentlyUsed = head.next;
        removeNode(leastRecentlyUsed);
        mapper.remove(leastRecentlyUsed.key);
        return leastRecentlyUsed.key;
    }

    @Override
    public void removeKey(K key) {
        if (mapper.containsKey(key)) {
            Node<K> node = mapper.get(key);
            removeNode(node);
            mapper.remove(key);
        }
    }

    private void removeNode(Node<K> node) {
        if (node != null && node.prev != null && node.next != null) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            node.prev = null;
            node.next = null;
        }
    }

    private void addNodeToTail(Node<K> node) {
        Node<K> last = tail.prev;
        last.next = node;
        node.prev = last;
        node.next = tail;
        tail.prev = node;
    }
}
