package com.dangerye.powerful.concurrent;

public interface InvokeConfig<C> extends AutoCloseable {
    void configure(C context);
}
