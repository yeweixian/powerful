package com.dangerye.powerful.collection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Objects;

public final class NewCollectionInvoker<T, C> {

    private final C context;
    private final Collection<InvokerInterceptor<T, C>> interceptors;
    private final Collection<AbstractFilter<T, C>> filters;

    private NewCollectionInvoker(C context,
                                 Collection<InvokerInterceptor<T, C>> interceptors,
                                 Collection<AbstractFilter<T, C>> filters) {
        this.context = context;
        this.interceptors = CollectionUtils.emptyIfNull(interceptors);
        this.filters = CollectionUtils.emptyIfNull(filters);
    }

    public static <T, C> NewCollectionInvokerBuilder<T, C> builder() {
        return new NewCollectionInvokerBuilder<>();
    }

    public void invoke(Collection<T> collection) {
        Assert.notNull(collection, "collection must not be null");
        Assert.notNull(context, "context must not be null");
        Runnable runnable = () -> {
            // filter 设置 所需 context
            filters.stream().filter(Objects::nonNull)
                    .forEach(item -> item.setContext(context));
            // 单元素过滤处理；
            try {
                final Predicate<T> allPredicate = PredicateUtils.allPredicate(filters);
                CollectionUtils.filter(collection, allPredicate);
            } finally {
                filters.stream().filter(Objects::nonNull)
                        .forEach(AbstractFilter::removeContext);
            }
        };
        for (InvokerInterceptor<T, C> interceptor : interceptors) {
            runnable = interceptor.plugin(new Invocation<>(runnable, collection, context));
        }
        runnable.run();
    }

    public interface InvokerInterceptor<T, C> {
        void intercept(Invocation<T, C> invocation);

        default Runnable plugin(Invocation<T, C> invocation) {
            return () -> this.intercept(invocation);
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

    @Getter
    @AllArgsConstructor
    public static class Invocation<T, C> {
        private final Runnable runnable;
        private final Collection<T> collection;
        private final C context;

        public void proceed() {
            runnable.run();
        }
    }

    public static class NewCollectionInvokerBuilder<T, C> {
        private C context;
        private Collection<InvokerInterceptor<T, C>> interceptors;
        private Collection<AbstractFilter<T, C>> filters;

        private NewCollectionInvokerBuilder() {
        }

        public NewCollectionInvokerBuilder<T, C> context(C context) {
            this.context = context;
            return this;
        }

        public NewCollectionInvokerBuilder<T, C> interceptors(Collection<InvokerInterceptor<T, C>> interceptors) {
            this.interceptors = interceptors;
            return this;
        }

        public NewCollectionInvokerBuilder<T, C> filters(Collection<AbstractFilter<T, C>> filters) {
            this.filters = filters;
            return this;
        }

        public NewCollectionInvoker<T, C> build() {
            return new NewCollectionInvoker<>(context, interceptors, filters);
        }
    }
}
