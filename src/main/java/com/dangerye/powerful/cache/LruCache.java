package com.dangerye.powerful.cache;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public final class LruCache<K, V> {

    private final int hotSize;
    private final long survivalTime;
    private final ReentrantLock lock;
    private final ConcurrentHashMap<K, Node<K, V>> cache;
    private final Node<K, V> begin;
    private final Node<K, V> end;

    private LruCache(int hotSize, long survivalTime) {
        this.hotSize = hotSize;
        this.survivalTime = survivalTime;
        this.lock = new ReentrantLock();
        this.cache = new ConcurrentHashMap<>(hotSize + 1);
        this.begin = new Node<>();
        this.end = new Node<>();
        begin.next = end;
        end.prev = begin;
    }

    public static <K, V> LruCache<K, V> initCache(int hotSize, long survivalTime) {
        return new LruCache<>(hotSize, survivalTime);
    }

    public V get(K key) {
        Supplier<V> resultSupplier = () -> null;
        return codeGet(key, resultSupplier);
    }

    public V get(K key, Supplier<V> supplier) {
        Supplier<V> resultSupplier = () -> {
            final V result = Optional.ofNullable(supplier)
                    .map(Supplier::get)
                    .orElse(null);
            if (result == null) {
                return null;
            } else {
                put(key, result);
                return result;
            }
        };
        return codeGet(key, resultSupplier);
    }

    private V codeGet(K key, Supplier<V> supplier) {
        long now = System.currentTimeMillis();
        final Node<K, V> target = cache.get(key);
        if (target == null) {
            return supplier.get();
        }
        if (target.expireTime < now) {
            removeCache(target);
            return supplier.get();
        } else {
            final V result = target.value;
            put(key, result);
            return result;
        }
    }

    public void put(K key, V value) {
        long expireTime = System.currentTimeMillis() + survivalTime;
        final Node<K, V> newNode = new Node<>(key, value, expireTime);
        lock.lock();
        try {
            final Node<K, V> oldNode = cache.put(key, newNode);
            setHead(newNode);
            if (oldNode != null) {
                removeNode(oldNode);
            } else {
                if (cache.size() > hotSize) {
                    final Node<K, V> lastNode = end.prev;
                    cache.remove(lastNode.key);
                    removeNode(lastNode);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private void removeCache(final Node<K, V> target) {
        lock.lock();
        try {
            cache.remove(target.key);
            removeNode(target);
        } finally {
            lock.unlock();
        }
    }

    private void setHead(Node<K, V> node) {
        final Node<K, V> next = begin.next;
        begin.next = node;
        node.prev = begin;
        node.next = next;
        next.prev = node;
    }

    private void removeNode(Node<K, V> node) {
        final Node<K, V> prev = node.prev;
        final Node<K, V> next = node.next;
        prev.next = next;
        next.prev = prev;
    }

    private static class Node<K, V> {
        private K key;
        private V value;
        private long expireTime;
        private Node<K, V> prev;
        private Node<K, V> next;

        public Node() {
        }

        public Node(K key, V value, long expireTime) {
            this.key = key;
            this.value = value;
            this.expireTime = expireTime;
        }
    }
}
