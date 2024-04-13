package com.dangerye.powerful.concurrent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangerye.powerful.utils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface InvokeInterceptorPool {

    Logger LOGGER = LoggerFactory.getLogger(InvokeInterceptorPool.class);

    InvokeInterceptor<InvokeDefaultContext> DEFAULT_INVOKE_TIME_INTERCEPTOR = new InvokeInterceptor<InvokeDefaultContext>() {
        @Override
        protected <R> R intercept(InvokeInvocation<R, InvokeDefaultContext> invocation) throws Exception {
            final String invokeSign = invocation.getContext().getInvokeSign();
            final long beginTime = System.currentTimeMillis();
            try {
                return invocation.proceed();
            } finally {
                final long endTime = System.currentTimeMillis();
                LogUtils.info(LOGGER, "DEFAULT_INVOKE_TIME_INTERCEPTOR",
                        "invokeSign:{}, beginTime:{}, endTime:{}, runTime:{}",
                        invokeSign, beginTime, endTime, (endTime - beginTime));
            }
        }

        @Override
        public void configure(InvokeDefaultContext context) {
            LogUtils.debug(LOGGER, "DEFAULT_INVOKE_TIME_INTERCEPTOR", "invoke configure");
        }

        @Override
        public void close() throws Exception {
            LogUtils.debug(LOGGER, "DEFAULT_INVOKE_TIME_INTERCEPTOR", "invoke close");
        }
    };

    InvokeInterceptor<InvokeVisitContext> INVOKE_VISIT_LOG_INTERCEPTOR = new InvokeInterceptor<InvokeVisitContext>() {
        @Override
        protected <R> R intercept(InvokeInvocation<R, InvokeVisitContext> invocation) throws Exception {
            final InvokeVisitContext invokeVisitContext = invocation.getContext();
            final String invokeSign = invokeVisitContext.getInvokeSign();
            final Map<String, Object> paramMap = invokeVisitContext.getParamMap();
            try {
                final R result = invocation.proceed();
                LogUtils.info(LOGGER, "INVOKE_VISIT_LOG_INTERCEPTOR",
                        "invokeSign:{}, paramMap:{}, result:{}",
                        invokeSign,
                        JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect),
                        JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
                return result;
            } catch (Exception e) {
                LogUtils.warn(LOGGER, "INVOKE_VISIT_LOG_INTERCEPTOR",
                        "invokeSign:{}, paramMap:{}",
                        invokeSign,
                        JSON.toJSONString(paramMap, SerializerFeature.DisableCircularReferenceDetect),
                        e);
                throw e;
            }
        }

        @Override
        public void configure(InvokeVisitContext context) {
            LogUtils.debug(LOGGER, "INVOKE_VISIT_LOG_INTERCEPTOR", "invoke configure");
        }

        @Override
        public void close() throws Exception {
            LogUtils.debug(LOGGER, "INVOKE_VISIT_LOG_INTERCEPTOR", "invoke close");
        }
    };
}
