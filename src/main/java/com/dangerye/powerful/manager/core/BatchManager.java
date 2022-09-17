package com.dangerye.powerful.manager.core;

import java.util.Map;

public abstract class BatchManager {
    public BatchManager(UniversalContext universalContext) {
        universalContext.putIfAbsent(getBusinessEvent(), this);
    }

    protected abstract String getBusinessEvent();

    protected abstract Object handleBusiness(Map<String, String> paramMap);
}
