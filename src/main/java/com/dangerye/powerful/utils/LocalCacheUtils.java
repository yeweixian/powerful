package com.dangerye.powerful.utils;

import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class LocalCacheUtils<T> {

    private static final ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(5);

    private final ReentrantLock lock;
    private final ConcurrentHashMap<String, Node<T>> cache;
    private final PriorityQueue<Node<T>> queue;

    private LocalCacheUtils(int initialCapacity) {
        lock = new ReentrantLock();
        cache = new ConcurrentHashMap<>(initialCapacity);
        queue = new PriorityQueue<>(initialCapacity);
        initPool();
    }

    private void initPool() {
        pool.scheduleWithFixedDelay(new NodeWork(), 5, 5, TimeUnit.SECONDS);
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
