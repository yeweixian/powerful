package com.dangerye.powerful.communicate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangerye.powerful.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public final class CallHandler {

    public static <R, T extends Throwable> R handle(CallSupplier<R, T> callSupplier, String supplier,
                                                    Map<String, Object> paramMap,
                                                    ThrowableConsumer throwableConsumer,
                                                    boolean showInfoMsg) {
        try {
            R result = callSupplier.get();
            if (showInfoMsg) {
                LogUtils.info(log, "CallHandler_execute_event",
                        supplier + " paramMap:{}, result:{}",
                        JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect),
                        JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
            }
            return result;
        } catch (Throwable t) {
            LogUtils.warn(log, "CallHandler_execute_fail",
                    supplier + " paramMap:{}",
                    JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect),
                    t);
            if (throwableConsumer != null) {
                throwableConsumer.accept(t);
            }
            return null;
        }
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
