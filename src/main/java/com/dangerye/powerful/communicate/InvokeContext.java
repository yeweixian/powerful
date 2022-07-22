package com.dangerye.powerful.communicate;

public interface InvokeContext<T> {
    String getInvokeEvent();

    T getTarget();
}
