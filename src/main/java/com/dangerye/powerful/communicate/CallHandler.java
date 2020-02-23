package com.dangerye.powerful.communicate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangerye.powerful.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public final class CallHandler<R> {

    private final Callable<R> codeFunction;
    private final Function<Consumer<Exception>, R> getFunction;
    private final ThrowFunction<R> throwFunction;

    private CallHandler(Callable<R> callSupplier, String supplier, Map<String, Object> paramMap, boolean showInfoMsg) {
        this.codeFunction = () -> {
            R result = callSupplier.call();
            if (showInfoMsg) {
                LogUtils.info(log, "CallHandler_execute_event",
                        supplier + " paramMap:{}, result:{}",
                        JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect),
                        JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
            }
            return result;
        };
        this.getFunction = consumer -> {
            try {
                return codeFunction.call();
            } catch (Exception e) {
                LogUtils.warn(log, "CallHandler_execute_fail",
                        supplier + " paramMap:{}",
                        JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect),
                        e);
                if (consumer != null) {
                    consumer.accept(e);
                }
                return null;
            }
        };
        this.throwFunction = new ThrowFunction<R>() {
            @Override
            public <E extends Throwable> R apply(Function<Exception, ? extends E> function) throws E {
                try {
                    return codeFunction.call();
                } catch (Exception e) {
                    LogUtils.warn(log, "CallHandler_execute_fail",
                            supplier + " paramMap:{}",
                            JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect),
                            e);
                    throw function.apply(e);
                }
            }
        };
    }

    public static <R> CallHandler<R> init(Callable<R> callSupplier, String supplier,
                                          Map<String, Object> paramMap, boolean showInfoMsg) {
        return new CallHandler<>(callSupplier, supplier, paramMap, showInfoMsg);
    }

    public R get() {
        return get(null);
    }

    public R get(Consumer<Exception> consumer) {
        return getFunction.apply(consumer);
    }

    public <E extends Throwable> R getOrThrow(Function<Exception, ? extends E> function) throws E {
        return throwFunction.apply(function);
    }

    public <E extends Throwable> R getElseThrow(Function<Exception, ? extends E> function) throws E {
        List<Exception> exceptionList = new ArrayList<>();
        return Optional.ofNullable(get(exceptionList::add))
                .orElseThrow(() -> {
                    Exception exception = exceptionList.size() > 0 ? exceptionList.get(0) : null;
                    return function.apply(exception);
                });
    }

    @FunctionalInterface
    private interface ThrowFunction<R> {
        <E extends Throwable> R apply(Function<Exception, ? extends E> function) throws E;
    }
}
