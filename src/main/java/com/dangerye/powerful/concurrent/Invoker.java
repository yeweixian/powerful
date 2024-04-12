package com.dangerye.powerful.concurrent;

import java.util.Collection;

public abstract class Invoker<C> {

    private final CodeFunc<C> codeFunc;

    protected Invoker() {
        codeFunc = new CodeFunc<C>() {
            @Override
            public <R> R execute(C context) throws Exception {
                final Collection<InvokeInterceptor<? super C>> interceptors = invokeInterceptors(context);

            }
        };
    }

    protected abstract Collection<InvokeInterceptor<? super C>> invokeInterceptors(final C context);

    @FunctionalInterface
    private interface CodeFunc<C> {
        <R> R execute(C context) throws Exception;
    }
}
