package com.dangerye.powerful.communicate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangerye.powerful.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface InvokeInterceptorPool {

    Logger LOGGER = LoggerFactory.getLogger(InvokeInterceptorPool.class);

    Invoker.Interceptor<Invoker.InvokeContext> CALL_TIME_INTERCEPTOR = new Invoker.Interceptor<Invoker.InvokeContext>() {
        @Override
        protected <R> R intercept(Invoker.Invocation<R, Invoker.InvokeContext> invocation) throws Exception {
            final String invokeEvent = invocation.getContext().getInvokeEvent();
            final long beginTime = System.currentTimeMillis();
            try {
                return invocation.proceed();
            } finally {
                final long endTime = System.currentTimeMillis();
                LogUtils.info(LOGGER, "CALL_TIME_INTERCEPTOR",
                        "invokeEvent:{}, beginTime:{}, endTime:{}, runTime:{}",
                        invokeEvent, beginTime, endTime, (endTime - beginTime));
            }
        }

        @Override
        public void configure(Invoker.InvokeContext context) {
            LogUtils.info(LOGGER, "CALL_TIME_INTERCEPTOR", "run configure.");
        }

        @Override
        public void close() {
            LogUtils.info(LOGGER, "CALL_TIME_INTERCEPTOR", "run close.");
        }
    };

    Invoker.Interceptor<Invoker.CallContext> CALL_LOG_INTERCEPTOR = new Invoker.Interceptor<Invoker.CallContext>() {
        @Override
        protected <R> R intercept(Invoker.Invocation<R, Invoker.CallContext> invocation) throws Exception {
            final Invoker.CallContext context = invocation.getContext();
            try {
                final R result = invocation.proceed();
                LogUtils.info(LOGGER, "CALL_LOG_INTERCEPTOR",
                        "invokeEvent:{}, param:{}, result:{}",
                        context.getInvokeEvent(),
                        JSON.toJSONString(context.getParamMap(), SerializerFeature.DisableCircularReferenceDetect),
                        JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
                return result;
            } catch (Exception e) {
                LogUtils.warn(LOGGER, "CALL_LOG_INTERCEPTOR",
                        "invokeEvent:{}, param:{}",
                        context.getInvokeEvent(),
                        JSON.toJSONString(context.getParamMap(), SerializerFeature.DisableCircularReferenceDetect),
                        e);
                throw e;
            }
        }

        @Override
        public void configure(Invoker.CallContext context) {
            LogUtils.info(LOGGER, "CALL_LOG_INTERCEPTOR", "run configure.");
        }

        @Override
        public void close() {
            LogUtils.info(LOGGER, "CALL_LOG_INTERCEPTOR", "run close.");
        }
    };
}
