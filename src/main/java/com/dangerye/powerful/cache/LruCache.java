package com.dangerye.powerful.cache;

import java.util.concurrent.ConcurrentHashMap;

public final class LruCache<K, V> {

    private final ConcurrentHashMap<K, Node<K, V>> cache;
    private final Node<K, V> begin;
    private final Node<K, V> end;
    private final int hotSize;
    private final long survivalTime;

    private LruCache(int hotSize, long survivalTime) {
        this.hotSize = hotSize;
        this.survivalTime = survivalTime;
        this.cache = new ConcurrentHashMap<>(hotSize);
        this.begin = new Node<>();
        this.end = new Node<>();
    }

    public static <K, V> LruCache<K, V> initCache(int hotSize, long survivalTime) {
        return new LruCache<>(hotSize, survivalTime);
    }

    private static class Node<K, V> {
        private K key;
        private V value;
        private Node<K, V> prev;
        private Node<K, V> next;
        private long expireTime;
    }
}
