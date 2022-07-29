package com.dangerye.powerful.communicate.http;

import com.dangerye.powerful.communicate.AbstractInvoker;
import com.dangerye.powerful.communicate.InvokeInterceptorPool;
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
        result.add(InvokeInterceptorPool.CALL_TIME_INTERCEPTOR);
        result.add(InvokeInterceptorPool.CALL_LOG_INTERCEPTOR);
        return result;
    }
}
