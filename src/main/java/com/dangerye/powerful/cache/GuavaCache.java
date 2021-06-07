package com.dangerye.powerful.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.ForwardingLoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GuavaCache extends ForwardingLoadingCache.SimpleForwardingLoadingCache<LoadingCacheKey<String>, GuavaCacheData> {

    public GuavaCache(GuavaCacheLoader guavaCacheLoader) {
        super(CacheBuilder.newBuilder()
                .maximumSize(2)
                .refreshAfterWrite(3, TimeUnit.MINUTES)
                .build(guavaCacheLoader));
    }

    public GuavaCacheData getGuavaCacheData(String msg) {
        final LoadingCacheKey<String> key = buildKey(msg);
        GuavaCacheData result = null;
        try {
            result = get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private LoadingCacheKey<String> buildKey(String msg) {
        final LoadingCacheKey<String> loadingCacheKey = new LoadingCacheKey<String>() {
            @Override
            public String generateRealKey() {
                return "GuavaCache";
            }
        };
        loadingCacheKey.putToContext("msg", msg);
        return loadingCacheKey;
    }

    public static class GuavaCacheLoader extends CacheLoader<LoadingCacheKey<String>, GuavaCacheData> {

        private final ExecutorService asyncCacheExecutor = Executors.newSingleThreadExecutor();

        @Override
        public GuavaCacheData load(LoadingCacheKey<String> key) throws Exception {
            return GuavaCacheData.builder()
                    .msg(Objects.toString(key.getContextValue("msg"), ""))
                    .createTime(System.currentTimeMillis())
                    .build();
        }

        @Override
        public ListenableFuture<GuavaCacheData> reload(LoadingCacheKey<String> key, GuavaCacheData oldValue) throws Exception {
            ListenableFutureTask<GuavaCacheData> task = ListenableFutureTask.create(() -> this.load(key));
            asyncCacheExecutor.submit(task);
            return task;
        }
    }
}
