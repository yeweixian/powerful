package com.dangerye.powerful.collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class CollectionInvoker<T, C> {

    private final C context;
    private final Collection<InvokerInterceptor<T, C>> interceptors;
    private final Consumer<Collection<T>> consumer;

    private CollectionInvoker(C context,
                              Collection<InvokerInterceptor<T, C>> interceptors,
                              Collection<AbstractFilter<T, C>> filters) {
        this.context = context;
        this.interceptors = CollectionUtils.emptyIfNull(interceptors);
        final Collection<AbstractFilter<T, C>> filterCollection = CollectionUtils.emptyIfNull(filters);
        this.consumer = collection -> {
            // filter 设置 所需 context
            filterCollection.stream().filter(Objects::nonNull)
                    .forEach(item -> item.setContext(context));
            // 单元素过滤处理；
            try {
                final Predicate<T> allPredicate = PredicateUtils.allPredicate(filterCollection);
                CollectionUtils.filter(collection, allPredicate);
            } finally {
                filterCollection.stream().filter(Objects::nonNull)
                        .forEach(AbstractFilter::removeContext);
            }
        };
    }

    public static <T, C> CollectionInvokerBuilder<T, C> builder() {
        return new CollectionInvokerBuilder<>();
    }

    public static <T, R> List<R> changeList(List<T> list, Function<T, R> mapper) {
        return ListUtils.emptyIfNull(list)
                .stream().filter(Objects::nonNull)
                .map(mapper).filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void invoke(Collection<T> collection) {
        Assert.notNull(collection, "collection must not be null");
        Assert.notNull(context, "context must not be null");
        Consumer<Collection<T>> plugin = consumer;
        for (InvokerInterceptor<T, C> interceptor : interceptors) {
            plugin = interceptor.plugin(plugin, context);
        }
        plugin.accept(collection);
    }

    public interface InvokerInterceptor<T, C> {
        void intercept(Invocation<T, C> invocation);

        default Consumer<Collection<T>> plugin(Consumer<Collection<T>> consumer, C context) {
            return collection -> this.intercept(new Invocation<>(consumer, collection, context));
        }
    }

    public static abstract class AbstractFilter<T, C> implements Predicate<T> {

        private final ThreadLocal<C> contextThreadLocal = new ThreadLocal<>();

        private void setContext(C context) {
            this.contextThreadLocal.set(context);
        }

        private void removeContext() {
            this.contextThreadLocal.remove();
        }

        @Override
        public boolean evaluate(T item) {
            if (item == null) {
                return false;
            }
            final C context = this.contextThreadLocal.get();
            Assert.notNull(context, "context must not be null");
            return doFilter(item, context);
        }

        protected abstract boolean doFilter(T item, C context);
    }

    public static class Invocation<T, C> {
        private final Consumer<Collection<T>> consumer;
        private final Collection<T> collection;
        private final C context;

        private Invocation(Consumer<Collection<T>> consumer, Collection<T> collection, C context) {
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

    public static class CollectionInvokerBuilder<T, C> {
        private C context;
        private Collection<InvokerInterceptor<T, C>> interceptors;
        private Collection<AbstractFilter<T, C>> filters;

        private CollectionInvokerBuilder() {
        }

        public CollectionInvokerBuilder<T, C> context(C context) {
            this.context = context;
            return this;
        }

        public CollectionInvokerBuilder<T, C> interceptors(Collection<InvokerInterceptor<T, C>> interceptors) {
            this.interceptors = interceptors;
            return this;
        }

        public CollectionInvokerBuilder<T, C> filters(Collection<AbstractFilter<T, C>> filters) {
            this.filters = filters;
            return this;
        }

        public CollectionInvoker<T, C> build() {
            return new CollectionInvoker<>(context, interceptors, filters);
        }
    }
}
