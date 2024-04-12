package com.dangerye.powerful.concurrent;

import java.util.ArrayList;
import java.util.Collection;

public final class InvokeSession<C> implements InvokeConfig<C> {

    private final Collection<InvokeConfig<? super C>> collection;

    private InvokeSession(Collection<InvokeConfig<? super C>> collection) {
        this.collection = collection;
    }

    public static <S> InvokeSession<S> init(final Collection<? extends InvokeConfig<? super S>> collection) {
        return new InvokeSession<>(getConfigures(collection));
    }

    private static <S> Collection<InvokeConfig<? super S>> getConfigures(final Collection<? extends InvokeConfig<? super S>> collection) {
        final Collection<InvokeConfig<? super S>> result = new ArrayList<>();
        if (collection != null) {
            for (InvokeConfig<? super S> invokeConfig : collection) {
                if (invokeConfig != null) {
                    result.add(invokeConfig);
                }
            }
        }
        return result;
    }

    @Override
    public void configure(C context) {
        if (collection != null) {
            for (InvokeConfig<? super C> invokeConfig : collection) {
                if (invokeConfig != null) {
                    invokeConfig.configure(context);
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        if (collection != null) {
            for (InvokeConfig<? super C> invokeConfig : collection) {
                if (invokeConfig != null) {
                    invokeConfig.close();
                }
            }
        }
    }
}
