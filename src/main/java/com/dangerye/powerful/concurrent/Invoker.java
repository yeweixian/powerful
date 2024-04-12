package com.dangerye.powerful.concurrent;

import java.util.Collection;
import java.util.concurrent.Callable;

public abstract class Invoker<C> {

    private final CodeFunc<C> codeFunc;

    protected Invoker() {
        codeFunc = new CodeFunc<C>() {
            @Override
            public <R> R execute(C context) throws Exception {
                final Collection<InvokeInterceptor<? super C>> interceptors = invokeInterceptors(context);
                try (final InvokeSession<C> session = InvokeSession.init(interceptors)) {
                    session.configure(context);
                    final Callable<R> core = () -> coreCode(context);
                    final Callable<R> proxy = getProxy(core, interceptors, context);
                    return proxy.call();
                }
            }
        };
    }

    private static <R, S> Callable<R> getProxy(final Callable<R> callable, final Collection<InvokeInterceptor<? super S>> interceptors, S context) {
        Callable<R> plugin = callable;
        if (interceptors != null) {
            for (InvokeInterceptor<? super S> interceptor : interceptors) {
                if (interceptor != null) {
                    plugin = interceptor.plugin(plugin, context);
                }
            }
        }
        return plugin;
    }

    protected final <R> R execute(C context) throws Exception {
        return codeFunc.execute(context);
    }

    protected abstract <R> R coreCode(final C context) throws Exception;

    protected abstract Collection<InvokeInterceptor<? super C>> invokeInterceptors(final C context);

    @FunctionalInterface
    private interface CodeFunc<C> {
        <R> R execute(C context) throws Exception;
    }
}
