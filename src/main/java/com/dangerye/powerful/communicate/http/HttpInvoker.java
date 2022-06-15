package com.dangerye.powerful.communicate.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dangerye.powerful.communicate.CallableUtils;
import com.dangerye.powerful.communicate.ProxyUtils;
import com.dangerye.powerful.utils.LogUtils;
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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public final class HttpInvoker extends CallableUtils<String, Exception> {

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

    private static final ProxyUtils.Interceptor HTTP_PROLOG_INTERCEPTOR = new ProxyUtils.Interceptor() {
        @Override
        protected <R> R intercept(ProxyUtils.Invocation<R> invocation) throws Exception {
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

    private HttpInvoker(final HttpContext httpContext) {
        super();
        Args.notNull(httpContext.getHttpRequest(), "HTTP request");
        final Callable<String> callable = () -> {
            try (CloseableHttpClient client = Optional.ofNullable(httpContext.getHttpClient()).orElseGet(HTTP_CLIENT_SUPPLIER)) {
                ResponseHandler<String> handler = Optional.ofNullable(httpContext.getResponseHandler()).orElse(DEFAULTRESPONSEHANDLER);
                return client.execute(httpContext.getHttpRequest(), handler);
            }
        };
        final Callable<String> proxy = ProxyUtils.getCallable(callable, Lists.newArrayList(HTTP_PROLOG_INTERCEPTOR), httpContext);
        init(proxy, Function.identity());
    }

    public static HttpInvoker execute(final HttpContext httpContext) {
        return new HttpInvoker(httpContext);
    }

    @Data
    @Builder
    public static class HttpContext implements ProxyUtils.Context {
        private CloseableHttpClient httpClient;
        private HttpUriRequest httpRequest;
        private ResponseHandler<String> responseHandler;
        private String supplier;
        private Map<String, Object> paramMap;
    }
}
