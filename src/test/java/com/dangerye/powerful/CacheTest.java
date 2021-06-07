package com.dangerye.powerful;

import com.alibaba.fastjson.JSON;
import com.dangerye.powerful.cache.GuavaCache;
import com.dangerye.powerful.cache.GuavaCacheData;
import org.junit.Test;

public class CacheTest {

    @Test
    public void testGuavaCache() {
        final GuavaCache.GuavaCacheLoader guavaCacheLoader = new GuavaCache.GuavaCacheLoader();
        final GuavaCache guavaCache = new GuavaCache(guavaCacheLoader);
        for (int i = 0; i < 10; i++) {
            final GuavaCacheData data = guavaCache.getGuavaCacheData(String.valueOf(i % 5));
            System.out.println(JSON.toJSONString(data));
        }
    }
}
