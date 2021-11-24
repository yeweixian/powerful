package com.dangerye.powerful.collection;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Objects;

public final class CollectionInvoker<T, C> {

    private final C context;
    private final Collection<InvokerInterceptor<T, C>> interceptors;
    private final Collection<Predicate<T>> filters;

    private CollectionInvoker(C context,
                              Collection<InvokerInterceptor<T, C>> interceptors,
                              Collection<Predicate<T>> filters) {
        this.context = context;
        this.interceptors = CollectionUtils.emptyIfNull(interceptors);
        this.filters = CollectionUtils.emptyIfNull(filters);
    }

    public void invoke(Collection<T> collection) {
        Assert.notNull(collection, "collection must not be null");
        Assert.notNull(context, "context must not be null");
        // 前置处理（用于批量请求获取某结果）
        beforeInvoke(collection, context);
        // 单元素过滤处理；
        final Predicate<T> allPredicate = PredicateUtils.allPredicate(filters);
        CollectionUtils.filter(collection, allPredicate);
        // 后置处理
        afterInvoke(collection, context);
    }

    private void beforeInvoke(Collection<T> collection, C context) {
        interceptors.stream().filter(Objects::nonNull)
                .forEach(interceptor -> interceptor.beforeInvoke(collection, context));
    }

    private void afterInvoke(Collection<T> collection, C context) {
        interceptors.stream().filter(Objects::nonNull)
                .forEach(interceptor -> interceptor.afterInvoke(collection, context));
    }

    public interface InvokerInterceptor<T, C> {
        // 前置处理
        void beforeInvoke(Collection<T> collection, C context);

        // 后置处理
        void afterInvoke(Collection<T> collection, C context);
    }
}
