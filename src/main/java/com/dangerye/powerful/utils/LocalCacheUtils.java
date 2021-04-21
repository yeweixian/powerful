package com.dangerye.powerful.utils;

import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public final class LocalCacheUtils<T> {

    private final ReentrantLock lock;
    private final ConcurrentHashMap<String, Node<T>> cache;
    private final PriorityQueue<Node<T>> queue;

    private LocalCacheUtils(int initialCapacity) {
        lock = new ReentrantLock();
        cache = new ConcurrentHashMap<>(initialCapacity);
        queue = new PriorityQueue<>(initialCapacity);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleWithFixedDelay(new NodeWork(), 5, 5, TimeUnit.SECONDS);
    }

    public static <T> LocalCacheUtils<T> initCache(int initialCapacity) {
        return new LocalCacheUtils<>(initialCapacity);
    }

    public void set(T value, long survivalTime) {
        String key = UUID.randomUUID().toString();
        long expireTime = System.currentTimeMillis() + survivalTime;
        Node<T> newNode = new Node<>(key, value, expireTime);
        lock.lock();
        try {
            Node<T> old = cache.put(key, newNode);
            queue.add(newNode);
            if (old != null) {
                queue.remove(old);
            }
        } finally {
            lock.unlock();
        }
    }

    public List<T> getAll() {
        long now = System.currentTimeMillis();
        lock.lock();
        try {
            return cache.values().stream()
                    .filter(Objects::nonNull)
                    .filter(item -> item.expireTime > now)
                    .map(item -> item.value)
                    .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    private static class Node<T> implements Comparable<Node<T>> {
        private final String key;
        private final T value;
        private final long expireTime;

        public Node(String key, T value, long expireTime) {
            this.key = key;
            this.value = value;
            this.expireTime = expireTime;
        }

        @Override
        public int compareTo(Node<T> o) {
            long l = this.expireTime - o.expireTime;
            if (l > 0) return 1;
            if (l < 0) return -1;
            return 0;
        }
    }

    private class NodeWork implements Runnable {
        @Override
        public void run() {
            long now = System.currentTimeMillis();
            while (true) {
                lock.lock();
                try {
                    Node<T> node = queue.peek();
                    if (node == null || node.expireTime > now) {
                        return;
                    }
                    cache.remove(node.key);
                    queue.poll();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
