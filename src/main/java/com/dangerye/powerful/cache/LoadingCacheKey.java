package com.dangerye.powerful.cache;

import java.util.HashMap;
import java.util.Map;

public abstract class LoadingCacheKey<K> {

    private K realKey;
    private Map<String, Object> loadingContext;

    public abstract K generateRealKey();

    public void putToContext(String contextKey, Object contextValue) {
        if (loadingContext == null) {
            loadingContext = new HashMap<>();
        }
        loadingContext.put(contextKey, contextValue);
    }

    public Object getContextValue(String contextKey) {
        return loadingContext.get(contextKey);
    }

    private K getRealKey() {
        if (realKey == null) {
            realKey = generateRealKey();
        }
        return realKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof LoadingCacheKey)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        LoadingCacheKey otherKey = (LoadingCacheKey) obj;
        return getRealKey().equals(otherKey.getRealKey());
    }

    @Override
    public int hashCode() {
        return getRealKey().hashCode();
    }
}
