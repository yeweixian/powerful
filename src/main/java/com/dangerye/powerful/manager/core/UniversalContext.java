package com.dangerye.powerful.manager.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public final class UniversalContext {

    private final Type type = new TypeReference<Map<String, String>>() {
    }.getType();
    private final Map<String, Function<String, String>> singleBusinessMap = new HashMap<>();
    private final Map<String, Function<Map<String, String>, Object>> batchBusinessMap = new HashMap<>();

    public void putIfAbsent(String event, SingleManager singleManager) {
        singleBusinessMap.putIfAbsent(event, singleManager::handleBusiness);
    }

    public void putIfAbsent(String event, BatchManager batchManager) {
        batchBusinessMap.putIfAbsent(event, batchManager::handleBusiness);
    }

    public Map<String, Function<String, String>> getSingleBusinessMap() {
        return singleBusinessMap;
    }

    public Map<String, Function<Map<String, String>, Object>> getBatchBusinessMap() {
        return batchBusinessMap;
    }

    public final Map<String, String> parseParam(String param) {
        if (StringUtils.isBlank(param)) {
            return null;
        }
        try {
            return JSON.parseObject(param, type);
        } catch (Exception e) {
            throw new IllegalArgumentException("param 参数异常");
        }
    }

    public final <P> P parseParam(String param, Class<P> clazz) {
        if (StringUtils.isBlank(param)) {
            return null;
        }
        try {
            return JSON.parseObject(param, clazz);
        } catch (Exception e) {
            throw new IllegalArgumentException("param 参数异常");
        }
    }

    public final String returnSuccessResponse(Object data) {
        final Response response = new Response();
        response.setCode(1);
        response.setMsg("success");
        response.setData(data);
        return JSON.toJSONString(response, SerializerFeature.DisableCircularReferenceDetect);
    }

    @Data
    public static class Response {
        private int code;
        private String msg;
        private String originalCode;
        private String originalMsg;
        private Object data;
    }
}
