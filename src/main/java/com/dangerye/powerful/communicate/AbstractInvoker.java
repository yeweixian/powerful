package com.dangerye.powerful.communicate;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractInvoker<C extends Invoker.Context, E extends Throwable> implements Invoker {

    private final GetFunction<C, E> getFunction;
    private final ThrowFunction<C, E> throwFunction;

    protected AbstractInvoker() {
        this.getFunction = new GetFunction<C, E>() {
            @Override
            @SuppressWarnings("unchecked")
            public <R> R handle(C context, Consumer<E> consumer) {
                try {
                    return (R) getProxy(() -> coreCode(context), context).call();
                } catch (Exception e) {
                    if (consumer != null) {
                        final E exception = transformException(context, e);
                        consumer.accept(exception);
                    }
                    return null;
                }
            }
        };
        this.throwFunction = new ThrowFunction<C, E>() {
            @Override
            @SuppressWarnings("unchecked")
            public <R, T extends Throwable> R handle(C context, Function<E, ? extends T> function) throws T {
                try {
                    return (R) getProxy(() -> coreCode(context), context).call();
                } catch (Exception e) {
                    final E exception = transformException(context, e);
                    throw function.apply(exception);
                }
            }
        };
    }

    private <R> Callable<R> getProxy(final Callable<R> callable, final C context) {
        Callable<R> plugin = callable;
        final Collection<Interceptor> interceptors = logicInterceptors(context);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                if (interceptor != null) {
                    plugin = interceptor.plugin(plugin, context);
                }
            }
        }
        return plugin;
    }

    protected abstract <R> R coreCode(final C context) throws Exception;

    protected abstract Collection<Interceptor> logicInterceptors(final C context);

    protected abstract E transformException(final C context, final Exception exception);

    @SuppressWarnings("unchecked")
    public final <R> R get(final C context) {
        return (R) getFunction.handle(context, null);
    }

    @SuppressWarnings("unchecked")
    public final <R> R get(final C context, final Consumer<E> consumer) {
        return (R) getFunction.handle(context, consumer);
    }

    @SuppressWarnings("unchecked")
    public final <R, T extends Throwable> R getOrThrow(final C context, final Function<E, ? extends T> function) throws T {
        return (R) throwFunction.handle(context, function);
    }

    @SuppressWarnings("unchecked")
    public final <R, T extends Throwable> R getElseThrow(final C context, final Function<E, ? extends T> function) throws T {
        final ExceptionBridging<E> bridging = new ExceptionBridging<>();
        return Optional.ofNullable((R) getFunction.handle(context, exception -> bridging.exception = exception))
                .orElseThrow(() -> function.apply(bridging.exception));
    }

    @FunctionalInterface
    private interface GetFunction<C extends Context, E extends Throwable> {
        <R> R handle(final C context, final Consumer<E> consumer);
    }

    @FunctionalInterface
    private interface ThrowFunction<C extends Context, E extends Throwable> {
        <R, T extends Throwable> R handle(final C context, final Function<E, ? extends T> function) throws T;
    }

    private static final class ExceptionBridging<E> {
        private E exception;
    }
}
