package com.dangerye.powerful.communicate;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public abstract class CallableUtils<R, E extends Throwable> {

    private final Function<Consumer<E>, R> getFunction;
    private final ThrowFunction<R, E> throwFunction;

    protected CallableUtils(final Callable<R> callable, final Function<Exception, E> changeException) {
        this.getFunction = consumer -> {
            try {
                return callable.call();
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
                    return callable.call();
                } catch (Exception e) {
                    final E ce = changeException.apply(e);
                    throw function.apply(ce);
                }
            }
        };
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
        final Context<E> context = new Context<>();
        return Optional.ofNullable(getFunction.apply(exception -> context.e = exception))
                .orElseThrow(() -> function.apply(context.e));
    }

    @FunctionalInterface
    private interface ThrowFunction<R, E extends Throwable> {
        <T extends Throwable> R apply(Function<E, ? extends T> function) throws T;
    }

    private static class Context<E> {
        private E e;
    }
}
