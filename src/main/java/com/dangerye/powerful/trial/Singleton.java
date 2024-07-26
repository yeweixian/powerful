package com.dangerye.powerful.trial;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public final class Singleton {
    private static final ConcurrentHashMap<Class<?>, Loader<?>> EXAMPLE_MAP = new ConcurrentHashMap<>();

    public static <C> C getInstance(Class<C> clazz) {
        if (clazz == null) {
            throw new NullPointerException();
        }
        final Loader<C> loader;
        final Loader<?> tryFirst = EXAMPLE_MAP.get(clazz);
        if (tryFirst == null) {
            synchronized (EXAMPLE_MAP) {
                final Loader<?> classLoader = EXAMPLE_MAP.get(clazz);
                if (classLoader == null) {
                    final Loader<C> cLoader = new Loader<>();
                    EXAMPLE_MAP.put(clazz, cLoader);
                    loader = cLoader;
                } else {
                    loader = (Loader<C>) classLoader;
                }
            }
        } else {
            loader = (Loader<C>) tryFirst;
        }
        final C tryGet = loader.get();
        if (tryGet == null) {
            synchronized (loader) {
                final C obj = loader.get();
                if (obj == null) {
                    final C instance = createInstance(clazz);
                    loader.set(instance);
                    return instance;
                } else {
                    return obj;
                }
            }
        } else {
            return tryGet;
        }
    }

    private static <C> C createInstance(Class<C> clazz) {
        try {
            final Method staticMethod = clazz.getDeclaredMethod("getInstance");
            staticMethod.setAccessible(true);
            final Object obj = staticMethod.invoke(null);
            return (C) obj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
