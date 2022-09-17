package com.dangerye.powerful.manager.core;

import java.util.Map;

public abstract class BatchManager {

    private final UniversalContext universalContext;

    public BatchManager(UniversalContext universalContext) {
        universalContext.putIfAbsent(getBusinessEvent(), this);
        this.universalContext = universalContext;
    }

    protected final UniversalContext getUniversalContext() {
        return universalContext;
    }

    protected abstract String getBusinessEvent();

    protected abstract Object handleBusiness(Map<String, String> paramMap);
}
