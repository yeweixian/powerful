package com.dangerye.powerful.communicate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangerye.powerful.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public final class CallHandler<R, T extends Throwable> {

    private final CallSupplier<R, T> callSupplier;
    private final String supplier;
    private final Map<String, Object> paramMap;
    private final boolean showInfoMsg;
    private final CallFunction<R, T> callFunction = (callSupplier, supplier, paramMap, showInfoMsg) -> {
        R result = callSupplier.get();
        if (showInfoMsg) {
            LogUtils.info(log, "CallHandler_execute_event",
                    supplier + " paramMap:{}, result:{}",
                    JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect),
                    JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
        }
        return result;
    };

    private CallHandler(CallSupplier<R, T> callSupplier, String supplier, Map<String, Object> paramMap, boolean showInfoMsg) {
        this.callSupplier = callSupplier;
        this.supplier = supplier;
        this.paramMap = paramMap;
        this.showInfoMsg = showInfoMsg;
    }

    public static <R, T extends Throwable> CallHandler<R, T> init(CallSupplier<R, T> callSupplier, String supplier,
                                                                  Map<String, Object> paramMap, boolean showInfoMsg) {
        return new CallHandler<>(callSupplier, supplier, paramMap, showInfoMsg);
    }

    public R get() {
        return get(null);
    }

    public R get(ThrowableConsumer throwableConsumer) {
        try {
            return callFunction.apply(callSupplier, supplier, paramMap, showInfoMsg);
        } catch (Throwable throwable) {
            LogUtils.warn(log, "CallHandler_execute_fail",
                    supplier + " paramMap:{}",
                    JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect),
                    throwable);
            if (throwableConsumer != null) {
                throwableConsumer.accept(throwable);
            }
            return null;
        }
    }

    public <E extends Throwable> R getOrThrow(ThrowableFunction<? extends E> throwableFunction) throws E {
        try {
            return callFunction.apply(callSupplier, supplier, paramMap, showInfoMsg);
        } catch (Throwable throwable) {
            LogUtils.warn(log, "CallHandler_execute_fail",
                    supplier + " paramMap:{}",
                    JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect),
                    throwable);
            throw throwableFunction.apply(throwable);
        }
    }

    @FunctionalInterface
    private interface CallFunction<R, T extends Throwable> {
        R apply(CallSupplier<R, T> callSupplier, String supplier,
                Map<String, Object> paramMap, boolean showInfoMsg) throws T;
    }

    @FunctionalInterface
    public interface CallSupplier<R, T extends Throwable> {
        R get() throws T;
    }

    @FunctionalInterface
    public interface ThrowableFunction<R extends Throwable> {
        R apply(Throwable throwable);
    }

    @FunctionalInterface
    public interface ThrowableConsumer {
        void accept(Throwable throwable);
    }
}
