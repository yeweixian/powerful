package com.dangerye.powerful.communicate.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangerye.powerful.communicate.AbstractInvoker;
import com.dangerye.powerful.utils.LogUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public final class HttpInvoker extends AbstractInvoker<String, Exception> {

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

    private static final ResponseHandler<String> DEFAULTRESPONSEHANDLER = httpResponse -> {
        int status = httpResponse.getStatusLine().getStatusCode();
        if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
            HttpEntity entity = httpResponse.getEntity();
            return entity != null ? EntityUtils.toString(entity) : null;
        } else {
            throw new ClientProtocolException("Unexpected response status: " + status);
        }
    };

    private static final Interceptor HTTP_CALLTIME_INTERCEPTOR = new Interceptor() {
        @Override
        protected <R> R intercept(Invocation<R> invocation) throws Exception {
            final long beginTime = System.currentTimeMillis();
            final R result = invocation.proceed();
            final long endTime = System.currentTimeMillis();
            LogUtils.info(log, "httpCallTime", "beginTime:{}ms, endTime:{}ms, runTime:{}ms",
                    beginTime, endTime, (endTime - beginTime));
            return result;
        }
    };

    private static final Interceptor HTTP_PROLOG_INTERCEPTOR = new Interceptor() {
        @Override
        protected <R> R intercept(Invocation<R> invocation) throws Exception {
            try {
                return invocation.proceed();
            } catch (Exception e) {
                LogUtils.warn(log, "httpCallFail",
                        Objects.toString(invocation.getContext().getSupplier(), "") + " param:{}",
                        JSON.toJSONString(invocation.getContext().getParamMap(), SerializerFeature.DisableCircularReferenceDetect), e);
                throw e;
            }
        }
    };

    @Override
    protected <C extends Context> String coreCode(C context) throws Exception {
        final HttpContext httpContext = check(context);
        Args.notNull(httpContext.getHttpRequest(), "HTTP request");
        try (CloseableHttpClient client = Optional.ofNullable(httpContext.getHttpClient()).orElseGet(HTTP_CLIENT_SUPPLIER)) {
            ResponseHandler<String> handler = Optional.ofNullable(httpContext.getResponseHandler()).orElse(DEFAULTRESPONSEHANDLER);
            return client.execute(httpContext.getHttpRequest(), handler);
        }
    }

    @Override
    protected <C extends Context> Collection<Interceptor> logicInterceptors(C context) {
        return Lists.newArrayList(HTTP_CALLTIME_INTERCEPTOR, HTTP_PROLOG_INTERCEPTOR);
        //return Lists.newArrayList(HTTP_PROLOG_INTERCEPTOR);
    }

    @Override
    protected <C extends Context> Exception transformException(C context, Exception exception) {
        return exception;
    }

    private HttpContext check(Context context) {
        Args.check(context instanceof HttpContext, "类型不匹配");
        return (HttpContext) context;
    }

    @Data
    @Builder
    public static class HttpContext implements Context {
        private String supplier;
        private CloseableHttpClient httpClient;
        private HttpUriRequest httpRequest;
        private ResponseHandler<String> responseHandler;

        @Override
        public Map<String, Object> getParamMap() {
            if (httpRequest != null) {
                return ImmutableMap.<String, Object>builder()
                        .put("method", Objects.toString(httpRequest.getMethod(), ""))
                        .put("uri", Objects.toString(httpRequest.getURI(), ""))
                        .build();
            }
            return null;
        }
    }
}
