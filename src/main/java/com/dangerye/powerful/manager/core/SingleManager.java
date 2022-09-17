package com.dangerye.powerful.manager.core;

public abstract class SingleManager {

    private final UniversalContext universalContext;

    public SingleManager(UniversalContext universalContext) {
        universalContext.putIfAbsent(getBusinessEvent(), this);
        this.universalContext = universalContext;
    }

    protected final UniversalContext getUniversalContext() {
        return universalContext;
    }

    protected abstract String getBusinessEvent();

    protected abstract String handleBusiness(String param);
}
