package com.dangerye.powerful.concurrent;

import java.util.Map;

public interface InvokeVisitContext extends InvokeDefaultContext {
    Map<String, Object> getParamMap();
}
