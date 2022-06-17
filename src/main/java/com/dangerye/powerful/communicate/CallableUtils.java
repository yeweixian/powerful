package com.dangerye.powerful.communicate;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class CallableUtils<R, E extends Throwable> {

    private Function<Consumer<E>, R> getFunction;
    private ThrowFunction<R, E> throwFunction;

    protected CallableUtils(Context context) {
        final Callable<R> callable = coreCode(context);
        final Supplier<Collection<Interceptor>> interceptorsSupplier = logicInterceptors(context);
        final Function<Exception, E> changeException = changeException(context);
        init(callable, interceptorsSupplier, changeException, context);
    }

    protected abstract Callable<R> coreCode(Context context);

    protected abstract Supplier<Collection<Interceptor>> logicInterceptors(Context context);

    protected abstract Function<Exception, E> changeException(Context context);

    private void init(final Callable<R> callable,
                      final Supplier<Collection<Interceptor>> interceptorsSupplier,
                      final Function<Exception, E> changeException,
                      final Context context) {
        this.getFunction = consumer -> {
            try {
                return getProxy(callable, interceptorsSupplier, context).call();
            } catch (Exception e) {
                if (consumer != null) {
                    final E ce = changeException.apply(e);
                    consumer.accept(ce);
                }
                return null;
            }
        };
        this.throwFunction = new ThrowFunction<R, E>() {
            @Override
            public <T extends Throwable> R apply(Function<E, ? extends T> function) throws T {
                try {
                    return getProxy(callable, interceptorsSupplier, context).call();
                } catch (Exception e) {
                    final E ce = changeException.apply(e);
                    throw function.apply(ce);
                }
            }
        };
    }

    private Callable<R> getProxy(final Callable<R> callable,
                                 final Supplier<Collection<Interceptor>> interceptorsSupplier,
                                 final Context context) {
        Callable<R> plugin = callable;
        final Collection<Interceptor> interceptors = Optional.ofNullable(interceptorsSupplier)
                .map(Supplier::get)
                .orElse(null);
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                if (interceptor != null) {
                    plugin = interceptor.plugin(plugin, context);
                }
            }
        }
        return plugin;
    }

    public final R get() {
        return getFunction.apply(null);
    }

    public final R get(final Consumer<E> consumer) {
        return getFunction.apply(consumer);
    }

    public final <T extends Throwable> R getOrThrow(final Function<E, ? extends T> function) throws T {
        return throwFunction.apply(function);
    }

    public final <T extends Throwable> R getElseThrow(final Function<E, ? extends T> function) throws T {
        final Bridging<E> bridging = new Bridging<>();
        return Optional.ofNullable(getFunction.apply(exception -> bridging.e = exception))
                .orElseThrow(() -> function.apply(bridging.e));
    }

    public interface Context {
    }

    @FunctionalInterface
    private interface ThrowFunction<R, E extends Throwable> {
        <T extends Throwable> R apply(Function<E, ? extends T> function) throws T;
    }

    public static abstract class Interceptor {
        protected abstract <R> R intercept(Invocation<R> invocation) throws Exception;

        private <R> Callable<R> plugin(Callable<R> plugin, Context context) {
            return () -> intercept(new Invocation<>(plugin, context));
        }
    }

    public static final class Invocation<R> {
        private final Callable<R> callable;
        private final Context context;

        private Invocation(Callable<R> callable, Context context) {
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

    private static final class Bridging<E> {
        private E e;
    }
}
