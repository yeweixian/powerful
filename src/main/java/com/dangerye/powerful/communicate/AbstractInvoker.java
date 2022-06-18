package com.dangerye.powerful.communicate;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractInvoker<E extends Throwable> {

    private final GetFunction<E> getFunction;
    private final ThrowFunction<E> throwFunction;

    protected AbstractInvoker() {
        this.getFunction = new GetFunction<E>() {
            @Override
            @SuppressWarnings("unchecked")
            public <C extends Context, R> R handle(C context, Consumer<E> consumer) {
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
        this.throwFunction = new ThrowFunction<E>() {
            @Override
            @SuppressWarnings("unchecked")
            public <C extends Context, R, T extends Throwable> R handle(C context, Function<E, ? extends T> function) throws T {
                try {
                    return (R) getProxy(() -> coreCode(context), context).call();
                } catch (Exception e) {
                    final E exception = transformException(context, e);
                    throw function.apply(exception);
                }
            }
        };
    }

    private <R> Callable<R> getProxy(final Callable<R> callable, final Context context) {
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

    protected abstract <C extends Context, R> R coreCode(final C context) throws Exception;

    protected abstract <C extends Context> Collection<Interceptor> logicInterceptors(final C context);

    protected abstract <C extends Context> E transformException(final C context, final Exception exception);

    @SuppressWarnings("unchecked")
    public final <C extends Context, R> R get(final C context) {
        return (R) getFunction.handle(context, null);
    }

    @SuppressWarnings("unchecked")
    public final <C extends Context, R> R get(final C context, final Consumer<E> consumer) {
        return (R) getFunction.handle(context, consumer);
    }

    @SuppressWarnings("unchecked")
    public final <C extends Context, R, T extends Throwable> R getOrThrow(final C context, final Function<E, ? extends T> function) throws T {
        return (R) throwFunction.handle(context, function);
    }

    @SuppressWarnings("unchecked")
    public final <C extends Context, R, T extends Throwable> R getElseThrow(final C context, final Function<E, ? extends T> function) throws T {
        final ExceptionBridging<E> bridging = new ExceptionBridging<>();
        return Optional.ofNullable((R) getFunction.handle(context, exception -> bridging.exception = exception))
                .orElseThrow(() -> function.apply(bridging.exception));
    }

    public interface Context {
        String getSupplier();

        Map<String, Object> getParamMap();
    }

    @FunctionalInterface
    private interface GetFunction<E extends Throwable> {
        <C extends Context, R> R handle(final C context, final Consumer<E> consumer);
    }

    @FunctionalInterface
    private interface ThrowFunction<E extends Throwable> {
        <C extends Context, R, T extends Throwable> R handle(final C context, final Function<E, ? extends T> function) throws T;
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
