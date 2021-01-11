package com.dangerye.powerful.communicate;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public final class ProxyUtils {

    public static <R> Callable<R> getCallable(Callable<R> callSupplier,
                                              String supplier,
                                              Map<String, Object> paramMap,
                                              List<Interceptor> interceptors) {
        for (Interceptor interceptor : interceptors) {
            callSupplier = interceptor.plugin(new Invocation<>(callSupplier, supplier, paramMap));
        }
        return callSupplier;
    }

    public interface Interceptor {
        <R> R intercept(Invocation<R> invocation) throws Exception;

        default <R> Callable<R> plugin(Invocation<R> invocation) {
            return () -> this.intercept(invocation);
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Invocation<R> {
        private final Callable<R> callSupplier;
        private final String supplier;
        private final Map<String, Object> paramMap;

        public R proceed() throws Exception {
            return callSupplier.call();
        }
    }
}
