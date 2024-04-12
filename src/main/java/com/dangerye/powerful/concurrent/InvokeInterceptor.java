package com.dangerye.powerful.concurrent;

import java.util.concurrent.Callable;

public abstract class InvokeInterceptor<C> implements InvokeConfig<C> {
    protected abstract <R> R intercept(final InvokeInvocation<R, C> invocation) throws Exception;

    <R> Callable<R> plugin(final Callable<R> plugin, C context) {
        return () -> intercept(new InvokeInvocation<>(plugin, context));
    }
}
