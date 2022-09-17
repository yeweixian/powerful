package com.dangerye.powerful.manager;

import com.dangerye.powerful.manager.core.UniversalContext;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Component
public final class UniversalManager {

    private final UniversalContext universalContext;

    @Autowired
    public UniversalManager(UniversalContext universalContext) {
        this.universalContext = universalContext;
    }

    public final String handleSingleBusiness(String event, String param) {
        final Map<String, Function<String, String>> singleBusinessMap = universalContext.getSingleBusinessMap();
        final Function<String, String> function = Optional.ofNullable(singleBusinessMap.get(event))
                .orElseThrow(() -> new IllegalArgumentException("event 事件异常"));
        return function.apply(param);
    }

    public final String handleBatchBusiness(String event, String param) {
        final Map<String, Function<Map<String, String>, Object>> batchBusinessMap = universalContext.getBatchBusinessMap();
        final Map<String, String> paramMap = MapUtils.emptyIfNull(universalContext.parseParam(param));
        final Map<String, Object> dataMap = new HashMap<>();
        Arrays.stream(StringUtils.defaultIfBlank(event, "").split(","))
                .forEach(subEvent -> Optional.ofNullable(batchBusinessMap.get(subEvent))
                        .ifPresent(function -> dataMap.computeIfAbsent(subEvent, mapKey -> function.apply(paramMap))));
        return universalContext.returnSuccessResponse(dataMap);
    }
}
