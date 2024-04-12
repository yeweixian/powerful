package com.dangerye.powerful.concurrent;

import java.util.concurrent.Callable;

public final class InvokeInvocation<R, C> {
    private final Callable<R> callable;
    private final C context;

    InvokeInvocation(Callable<R> callable, C context) {
        this.callable = callable;
        this.context = context;
    }

    public C getContext() {
        return context;
    }

    public R proceed() throws Exception {
        return callable.call();
    }
}
