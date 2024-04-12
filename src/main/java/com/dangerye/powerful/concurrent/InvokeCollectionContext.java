package com.dangerye.powerful.concurrent;

import java.util.Collection;

public interface InvokeCollectionContext<I> extends InvokeDefaultContext {
    Collection<I> getCollection();
}
