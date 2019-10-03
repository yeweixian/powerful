package com.dangerye.powerful.communicate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ThreadContext {

    private static final String TRACE_ID = "TRACE_ID";
    private static final String REQUEST_IP = "REQUEST_IP";

    private static final ThreadLocal<Map<String, String>> threadLocal
            = new ThreadLocal<>();

    public static void init() {
        threadLocal.set(new HashMap<>());
    }

    public static void close() {
        threadLocal.get().clear();
        threadLocal.remove();
    }

    public static void set(String key, String value) {
        Optional.of(threadLocal)
                .map(ThreadLocal::get)
                .ifPresent(map -> map.put(key, value));
    }

    public static String get(String key) {
        return Optional.of(threadLocal)
                .map(ThreadLocal::get)
                .map(map -> map.get(key))
                .orElse(null);
    }

    public static String getTraceId() {
        return get(TRACE_ID);
    }

    public static void setTraceId(String traceId) {
        set(TRACE_ID, traceId);
    }

    public static String getRequestIp() {
        return get(REQUEST_IP);
    }

    public static void setRequestIp(String requestIp) {
        set(REQUEST_IP, requestIp);
    }
}
