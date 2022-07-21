package com.dangerye.powerful.trial;

public interface InvokeContext<T> {
    String getInvokeEvent();

    T getTarget();
}
