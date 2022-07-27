package com.dangerye.powerful.communicate.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangerye.powerful.communicate.AbstractInvoker;
import com.dangerye.powerful.communicate.InvokeContext;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public final class HttpInvoker extends AbstractInvoker<Map<String, Object>, HttpContext, Exception> {

    private static final Supplier<CloseableHttpClient> HTTP_CLIENT_SUPPLIER = () -> {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1000)
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .build();
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(requestConfig)
                .build();
    };

    private static final ResponseHandler<String> DEFAULT_RESPONSE_HANDLER = httpResponse -> {
        int status = httpResponse.getStatusLine().getStatusCode();
        if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
            HttpEntity entity = httpResponse.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    };

    private static final Interceptor<InvokeContext<Map<String, Object>>> CALL_TIME_INTERCEPTOR = new Interceptor<InvokeContext<Map<String, Object>>>() {
        @Override
        protected <R> R intercept(Invocation<R, InvokeContext<Map<String, Object>>> invocation) throws Exception {
            final InvokeContext<Map<String, Object>> context = invocation.getContext();
            final long beginTime = System.currentTimeMillis();
            try {
                return invocation.proceed();
            } finally {
                final long endTime = System.currentTimeMillis();
                log.info("[CALL_TIME_INTERCEPTOR] msg= invokeEvent:{} - beginTime:{}ms, endTime:{}ms, runTime:{}",
                        context.getInvokeEvent(), beginTime, endTime, (endTime - beginTime));
            }
        }

        @Override
        public void configure(InvokeContext<Map<String, Object>> context) {
            System.out.println("configure : CALL_TIME_INTERCEPTOR");
        }

        @Override
        public void close() {
            System.out.println("close : CALL_TIME_INTERCEPTOR");
        }
    };

    private static final Interceptor<InvokeContext<Map<String, Object>>> CALL_LOG_INTERCEPTOR = new Interceptor<InvokeContext<Map<String, Object>>>() {
        @Override
        protected <R> R intercept(Invocation<R, InvokeContext<Map<String, Object>>> invocation) throws Exception {
            final InvokeContext<Map<String, Object>> context = invocation.getContext();
            try {
                final R result = invocation.proceed();
                log.info("[CALL_LOG_INTERCEPTOR] msg= invokeEvent:{} - param:{}, result:{}",
                        context.getInvokeEvent(),
                        JSON.toJSONString(context.getTarget(), SerializerFeature.DisableCircularReferenceDetect),
                        JSON.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect));
                return result;
            } catch (Exception e) {
                log.warn("[CALL_LOG_INTERCEPTOR] msg= invokeEvent:{} run fail - param:{}",
                        context.getInvokeEvent(),
                        JSON.toJSONString(context.getTarget(), SerializerFeature.DisableCircularReferenceDetect),
                        e);
                throw e;
            }
        }

        @Override
        public void configure(InvokeContext<Map<String, Object>> context) {
            System.out.println("configure : CALL_LOG_INTERCEPTOR");
        }

        @Override
        public void close() {
            System.out.println("close : CALL_LOG_INTERCEPTOR");
        }
    };

    @Override
    protected Exception transformException(HttpContext context, Exception exception) {
        return exception;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <R> R coreCode(HttpContext context) throws Exception {
        try (CloseableHttpClient client = Optional.ofNullable(context.getHttpClient()).orElseGet(HTTP_CLIENT_SUPPLIER)) {
            ResponseHandler<String> handler = Optional.ofNullable(context.getResponseHandler()).orElse(DEFAULT_RESPONSE_HANDLER);
            return (R) client.execute(context.getHttpRequest(), handler);
        }
    }

    @Override
    protected Collection<Interceptor<? super HttpContext>> invokeInterceptors(HttpContext context) {
        final Collection<Interceptor<? super HttpContext>> result = Lists.newArrayList();
        result.add(CALL_TIME_INTERCEPTOR);
        result.add(CALL_LOG_INTERCEPTOR);
        return result;
    }
}
