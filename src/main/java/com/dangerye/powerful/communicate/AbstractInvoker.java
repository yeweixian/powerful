package com.dangerye.powerful.communicate;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractInvoker<R, E extends Throwable> {

    private final GetFunction<R, E> getFunction;
    private final ThrowFunction<R, E> throwFunction;

    protected AbstractInvoker() {
        this.getFunction = new GetFunction<R, E>() {
            @Override
            public <C extends Context> R handle(C context, Consumer<E> consumer) {
                try {
                    return getProxy(() -> coreCode(context), context).call();
                } catch (Exception e) {
                    if (consumer != null) {
                        final E exception = transformException(context, e);
                        consumer.accept(exception);
                    }
                    return null;
                }
            }
        };
        this.throwFunction = new ThrowFunction<R, E>() {
            @Override
            public <C extends Context, T extends Throwable> R handle(C context, Function<E, ? extends T> function) throws T {
                try {
                    return getProxy(() -> coreCode(context), context).call();
                } catch (Exception e) {
                    final E exception = transformException(context, e);
                    throw function.apply(exception);
                }
            }
        };
    }

    private Callable<R> getProxy(final Callable<R> callable, final Context context) {
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

    protected abstract <C extends Context> R coreCode(final C context) throws Exception;

    protected abstract <C extends Context> Collection<Interceptor> logicInterceptors(final C context);

    protected abstract <C extends Context> E transformException(final C context, final Exception exception);

    public final <C extends Context> R get(final C context) {
        return getFunction.handle(context, null);
    }

    public final <C extends Context> R get(final C context, final Consumer<E> consumer) {
        return getFunction.handle(context, consumer);
    }

    public final <C extends Context, T extends Throwable> R getOrThrow(final C context, final Function<E, ? extends T> function) throws T {
        return throwFunction.handle(context, function);
    }

    public final <C extends Context, T extends Throwable> R getElseThrow(final C context, final Function<E, ? extends T> function) throws T {
        final ExceptionBridging<E> bridging = new ExceptionBridging<>();
        return Optional.ofNullable(getFunction.handle(context, exception -> bridging.exception = exception))
                .orElseThrow(() -> function.apply(bridging.exception));
    }

    public interface Context {
    }

    @FunctionalInterface
    private interface GetFunction<R, E extends Throwable> {
        <C extends Context> R handle(final C context, final Consumer<E> consumer);
    }

    @FunctionalInterface
    private interface ThrowFunction<R, E extends Throwable> {
        <C extends Context, T extends Throwable> R handle(final C context, final Function<E, ? extends T> function) throws T;
    }

    private static final class ExceptionBridging<E> {
        private E exception;
    }

    public static abstract class Interceptor {
        protected abstract <R> R intercept(final Invocation<R> invocation) throws Exception;

        private <R> Callable<R> plugin(final Callable<R> plugin, final Context context) {
            return () -> intercept(new Invocation<>(plugin, context));
        }
    }

    public static final class Invocation<R> {
        private final Callable<R> callable;
        private final Context context;

        private Invocation(final Callable<R> callable, final Context context) {
            this.callable = callable;
            this.context = context;
        }

        public Context getContext() {
            return context;
        }

        public R proceed() throws Exception {
            return callable.call();
        }
    }
}
