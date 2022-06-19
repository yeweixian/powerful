package com.dangerye.powerful.communicate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class AbstractCollectionInvoker<T, C extends Invoker.CollectionContext> implements Invoker {

    protected abstract Collection<CollectionInterceptor<T, C>> collectionBusinessInterceptors(final C context);

    protected abstract Collection<CollectionFilter<T, C>> collectionBusinessFilters(final C context);

    public final void execute(Collection<T> coll, C context) {
        Assert.notNull(coll, "collection must not be null");
        Assert.notNull(context, "context must not be null");
        final Collection<CollectionFilter<T, C>> filters = CollectionUtils.emptyIfNull(collectionBusinessFilters(context));
        Consumer<Collection<T>> plugin = collection -> {
            filters.stream().filter(Objects::nonNull)
                    .forEach(item -> item.setContext(context));
            try {
                final Predicate<T> allPredicate = PredicateUtils.allPredicate(filters);
                CollectionUtils.filter(collection, allPredicate);
            } finally {
                filters.stream().filter(Objects::nonNull)
                        .forEach(CollectionFilter::removeContext);
            }
        };
        final Collection<CollectionInterceptor<T, C>> interceptors = CollectionUtils.emptyIfNull(collectionBusinessInterceptors(context));
        for (CollectionInterceptor<T, C> interceptor : interceptors) {
            plugin = interceptor.plugin(plugin, context);
        }
        plugin.accept(coll);
    }
}
