package com.dangerye.powerful.concurrent;

import java.util.Collection;

public interface InvokeConfig<C> extends AutoCloseable {
    static <S> Collection<InvokeConfig<? super S>> getConfigures(final Collection<? extends InvokeConfig<? super S>> collection) {
    }

    void configure(C context);
}
