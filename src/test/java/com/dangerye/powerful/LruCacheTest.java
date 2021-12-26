package com.dangerye.powerful;

import com.dangerye.powerful.cache.LruCache;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;

public class LruCacheTest {

    @Test
    public void testCache() {
        final LruCache<Integer, Integer> cache = LruCache.initCache(2, 1000L * 10);
        for (int i = 0; i < 10; i++) {
            final int key = RandomUtils.nextInt(0, 4);
            final int value = RandomUtils.nextInt(100, 1000);
            System.out.printf("key:%s, value:%s%n", key, value);
            cache.put(key, value);
        }
        for (int i = 0; i < 4; i++) {
            System.out.println(cache.get(i));
        }
    }
}
