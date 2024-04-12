package com.dangerye.powerful.concurrent;


import org.apache.commons.collections4.Predicate;
import org.springframework.util.Assert;

public abstract class InvokeCollectionItemPredicate<I, C> implements Predicate<I>, InvokeConfig<C> {
    private final ThreadLocal<C> threadLocal = new ThreadLocal<>();

    @Override
    public void configure(C context) {
        threadLocal.set(context);
    }

    @Override
    public void close() throws Exception {
        threadLocal.remove();
    }

    @Override
    public boolean evaluate(I item) {
        if (item == null) {
            return false;
        }
        final C context = threadLocal.get();
        Assert.notNull(context, "context must not be null");
        return doFilter(item, context);
    }

    protected abstract boolean doFilter(I item, C context);
}
