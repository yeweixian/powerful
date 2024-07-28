package com.dangerye.powerful.trial;

import java.lang.reflect.Method;

public final class Loader<C> {
    private final Class<C> clazz;
    private volatile C instance;

    public Loader(Class<C> clazz) {
        this.clazz = clazz;
    }

    public void set(C instance) {
        this.instance = instance;
    }

    public C get() {
        return instance;
    }

    public C getInstance() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    return instance = createInstance();
                } else {
                    return instance;
                }
            }
        } else {
            return instance;
        }
    }

    @SuppressWarnings("unchecked")
    private C createInstance() {
        try {
            final Method staticMethod = clazz.getDeclaredMethod("getInstance");
            staticMethod.setAccessible(true);
            return (C) staticMethod.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
