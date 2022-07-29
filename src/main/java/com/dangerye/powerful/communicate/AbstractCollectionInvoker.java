package com.dangerye.powerful.communicate;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.springframework.util.Assert;

import java.util.Collection;

@Slf4j
public abstract class AbstractCollectionInvoker<I, C extends Invoker.CollectionContext<I>> extends Invoker<C> {

    protected abstract Collection<CollectionFilter<? super I, ? super C>> invokeCollectionFilters(final C context);

    private void writeLog(C context, Exception exception) {
        if (log.isDebugEnabled()) {
            final String invokeEvent = context.getInvokeEvent();
            log.debug("[CollectionInvoker.Fail] msg = invokeEvent:{} invoker fail. ", invokeEvent, exception);
        }
    }

    @Override
    protected <R> R coreCode(C context) throws Exception {
        final Collection<? extends I> target = context.getCollection();
        final Collection<CollectionFilter<? super I, ? super C>> collectionFilters = invokeCollectionFilters(context);
        try (CloseableContext<C> closeableContext = new CloseableContext<>(getConfigures(collectionFilters))) {
            closeableContext.configure(context);
            final Predicate<I> allPredicate = PredicateUtils.allPredicate(collectionFilters);
            CollectionUtils.filter(target, allPredicate);
        }
        return null;
    }

    public final void invoke(final C context) {
        Assert.notNull(context, "context must not be null");
        Assert.notNull(context.getCollection(), "collection must not be null");
        Assert.notNull(context.getInvokeEvent(), "invokeEvent must not be null");
        try {
            super.execute(context);
        } catch (Exception e) {
            writeLog(context, e);
        }
    }
}
