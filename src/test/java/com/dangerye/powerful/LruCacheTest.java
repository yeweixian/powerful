package com.dangerye.powerful;

import com.dangerye.powerful.cache.LruCache;
import org.junit.Test;

public class LruCacheTest {

    @Test
    public void testCache() {
        final LruCache<Integer, Integer> cache = LruCache.initCache(0, 1000L * 10);
        for (int i = 0; i < 10; i++) {
            cache.put(i, i);
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(cache.get(i));
        }
    }
}
