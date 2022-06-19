package com.dangerye.powerful.communicate;

import org.apache.commons.collections4.Predicate;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

public interface Invoker {

    interface Context {
        String getSupplier();

        Map<String, Object> getParamMap();
    }

    interface CollectionContext {
        String getBusinessEvent();
    }

    abstract class CollectionFilter<T, C extends CollectionContext> implements Predicate<T> {
        private final ThreadLocal<C> threadLocal = new ThreadLocal<>();

        void setContext(final C context) {
            threadLocal.set(context);
        }

        void removeContext() {
            threadLocal.remove();
        }

        @Override
        public boolean evaluate(T item) {
            if (item == null) {
                return false;
            }
            final C context = threadLocal.get();
            Assert.notNull(context, "context must not be null");
            return doFilter(item, context);
        }

        protected abstract boolean doFilter(T item, C context);
    }

    abstract class Interceptor {
        protected abstract <R, C extends Context> R intercept(final Invocation<R, C> invocation) throws Exception;

        <R, C extends Context> Callable<R> plugin(final Callable<R> plugin, final C context) {
            return () -> intercept(new Invocation<>(plugin, context));
        }
    }

    abstract class CollectionInterceptor<T, C extends CollectionContext> {
        protected abstract void intercept(final CollectionInvocation<T, C> invocation);

        Consumer<Collection<T>> plugin(final Consumer<Collection<T>> consumer, final C context) {
            return collection -> intercept(new CollectionInvocation<>(consumer, collection, context));
        }
    }

    final class Invocation<R, C extends Context> {
        private final Callable<R> callable;
        private final C context;

        private Invocation(Callable<R> callable, C context) {
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

    final class CollectionInvocation<T, C extends CollectionContext> {
        private final Consumer<Collection<T>> consumer;
        private final Collection<T> collection;
        private final C context;

        private CollectionInvocation(Consumer<Collection<T>> consumer, Collection<T> collection, C context) {
            this.consumer = consumer;
            this.collection = collection;
            this.context = context;
        }

        public Collection<T> getCollection() {
            return collection;
        }

        public C getContext() {
            return context;
        }

        public void proceed() {
            consumer.accept(collection);
        }
    }
}
