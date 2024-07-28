package com.dangerye.powerful.trial;

import java.util.concurrent.ConcurrentHashMap;

public final class SingletonLoader {
    private static final ConcurrentHashMap<Class<?>, Loader<?>> INSTANCE_MAP = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <C> Loader<C> getLoader(Class<C> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        final Loader<?> tryFirst = INSTANCE_MAP.get(clazz);
        if (tryFirst == null) {
            synchronized (INSTANCE_MAP) {
                final Loader<?> loader = INSTANCE_MAP.get(clazz);
                if (loader == null) {
                    final Loader<C> cLoader = new Loader<>(clazz);
                    INSTANCE_MAP.putIfAbsent(clazz, cLoader);
                    return cLoader;
                } else {
                    return (Loader<C>) loader;
                }
            }
        } else {
            return (Loader<C>) tryFirst;
        }
    }

    public static <C> C getInstance(Class<C> clazz) {
        final Loader<C> loader = getLoader(clazz);
        return loader.getInstance();
    }
}
