package com.dangerye.powerful.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.PredicateUtils;
import org.springframework.util.Assert;

import java.util.Collection;

@Slf4j
public abstract class AbstractCollectionInvoker<I, C extends InvokeCollectionContext<I>> extends Invoker<C> {

    protected abstract void increaseLog(final C context, final Exception exception);

    protected abstract Collection<InvokeCollectionItemPredicate<? super I, ? super C>> invokeCollectionItemPredicates(final C context);

    private void writeLog(C context, Exception exception) {
        if (log.isDebugEnabled()) {
            final String invokeSign = context.getInvokeSign();
            log.debug("[CollectionInvoker.Fail] msg = invokeSign:{} invoker fail. ", invokeSign, exception);
        }
        increaseLog(context, exception);
    }

    @Override
    protected <R> R coreCode(C context) throws Exception {
        final Collection<I> collection = context.getCollection();
        final Collection<InvokeCollectionItemPredicate<? super I, ? super C>> itemPredicates = invokeCollectionItemPredicates(context);
        try (final InvokeSession<C> session = InvokeSession.init(itemPredicates)) {
            session.configure(context);
            final Predicate<I> allPredicate = PredicateUtils.allPredicate(itemPredicates);
            CollectionUtils.filter(collection, allPredicate);
        }
        return null;
    }

    public final void invoke(final C context) {
        Assert.notNull(context, "context must not be null");
        Assert.notNull(context.getInvokeSign(), "invokeSign must not be null");
        Assert.notNull(context.getCollection(), "collection must not be null");
        try {
            super.execute(context);
        } catch (Exception e) {
            writeLog(context, e);
        }
    }
}
