package com.dangerye.powerful.manager.core;

public abstract class SingleManager {
    public SingleManager(UniversalContext universalContext) {
        universalContext.putIfAbsent(getBusinessEvent(), this);
    }

    protected abstract String getBusinessEvent();

    protected abstract String handleBusiness(String param);
}
