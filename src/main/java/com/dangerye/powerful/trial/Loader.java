package com.dangerye.powerful.trial;

public final class Loader<T> {
    private volatile T obj;

    public void set(T obj) {
        this.obj = obj;
    }

    public T get() {
        return obj;
    }
}
