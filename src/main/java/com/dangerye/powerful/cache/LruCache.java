package com.dangerye.powerful.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class LruCache<K, V> {

    private final ReentrantLock lock;
    private final ConcurrentHashMap<K, Node<K, V>> cache;
    private final Node<K, V> begin;
    private final Node<K, V> end;
    private final int hotSize;
    private final long survivalTime;

    private LruCache(int hotSize, long survivalTime) {
        this.hotSize = hotSize;
        this.survivalTime = survivalTime;
        this.lock = new ReentrantLock();
        this.cache = new ConcurrentHashMap<>(hotSize + 1);
        this.begin = new Node<>();
        this.end = new Node<>();
    }

    public static <K, V> LruCache<K, V> initCache(int hotSize, long survivalTime) {
        return new LruCache<>(hotSize, survivalTime);
    }

    public void put(K key, V value) {
        long expireTime = System.currentTimeMillis() + survivalTime;

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
