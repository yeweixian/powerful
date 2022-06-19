package com.dangerye.powerful.communicate.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangerye.powerful.communicate.AbstractInvoker;
import com.dangerye.powerful.utils.LogUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public final class HttpInvoker extends AbstractInvoker<HttpContext, Exception> {

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

    private static final Interceptor CALL_TIME_INTERCEPTOR = new Interceptor() {
        @Override
        protected <R, C extends Context> R intercept(Invocation<R, C> invocation) throws Exception {
            final C context = invocation.getContext();
            final long beginTime = System.currentTimeMillis();
            final R result = invocation.proceed();
            final long endTime = System.currentTimeMillis();
            LogUtils.info(log, context.getSupplier() + ".invoke",
                    "beginTime:{}ms, endTime:{}ms, runTime:{}ms",
                    beginTime, endTime, (endTime - beginTime));
            return result;
        }
    };

    private static final Interceptor PRO_LOG_INTERCEPTOR = new Interceptor() {
        @Override
        protected <R, C extends Context> R intercept(Invocation<R, C> invocation) throws Exception {
            final C context = invocation.getContext();
            try {
                return invocation.proceed();
            } catch (Exception e) {
                LogUtils.warn(log, context.getSupplier() + ".invokeFail",
                        "param:{}",
                        JSON.toJSONString(context.getParamMap(), SerializerFeature.DisableCircularReferenceDetect),
                        e);
                throw e;
            }
        }
    };

    @Override
    @SuppressWarnings("unchecked")
    protected <R> R coreCode(HttpContext context) throws Exception {
        Args.notNull(context.getHttpRequest(), "HTTP request");
        try (CloseableHttpClient client = Optional.ofNullable(context.getHttpClient()).orElseGet(HTTP_CLIENT_SUPPLIER)) {
            ResponseHandler<String> handler = Optional.ofNullable(context.getResponseHandler()).orElse(DEFAULT_RESPONSE_HANDLER);
            return (R) client.execute(context.getHttpRequest(), handler);
        }
    }

    @Override
    protected Collection<Interceptor> logicInterceptors(HttpContext context) {
        return Lists.newArrayList(CALL_TIME_INTERCEPTOR, PRO_LOG_INTERCEPTOR);
    }

    @Override
    protected Exception transformException(HttpContext context, Exception exception) {
        return exception;
    }
}
