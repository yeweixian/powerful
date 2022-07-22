package com.dangerye.powerful.communicate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;

import java.util.Collection;

@Slf4j
public abstract class AbstractCollectionInvoker<I, T extends Iterable<? extends I>, C extends InvokeContext<? extends T>> extends Invoker<T, C> {

    protected abstract Collection<CollectionFilter<I, C>> invokeCollectionFilters(final C context);

    private void writeLog(C context, Exception exception) {
        if (log.isWarnEnabled()) {
            final String invokeEvent = context.getInvokeEvent();
            log.warn("[CollectionInvoker.Fail] msg = invokeEvent:{} invoker fail. ", invokeEvent, exception);
        }
    }

    @Override
    protected <R> R coreCode(C context) throws Exception {
        final Iterable<? extends I> target = context.getTarget();
        final Collection<CollectionFilter<I, C>> collectionFilters = invokeCollectionFilters(context);
        try (CloseableContext<C> closeableContext = new CloseableContext<>(getConfigures(collectionFilters))) {
            closeableContext.configure(context);
            final Predicate<I> allPredicate = PredicateUtils.allPredicate(collectionFilters);
            CollectionUtils.filter(target, allPredicate);
        }
        return null;
    }

    public final void invoke(final C context) {
        try {
            super.execute(context);
        } catch (Exception e) {
            writeLog(context, e);
        }
    }
}
